package klaxon.klaxon.arthritis;

import klaxon.klaxon.arthritis.api.CreakEntrypoint;
import klaxon.klaxon.arthritis.api.CreakModule;
import klaxon.klaxon.arthritis.api.MissingHandler;
import klaxon.klaxon.arthritis.api.config.ConfigHandler;
import klaxon.klaxon.arthritis.io.RegistrySerializer;
import klaxon.klaxon.arthritis.io.MappingSerializer;
import klaxon.klaxon.arthritis.io.data.CacheInfo;
import klaxon.klaxon.arthritis.registry.RegistryFactory;
import klaxon.klaxon.arthritis.registry.RegistryReader;
import klaxon.klaxon.arthritis.registry.data.StageData;
import klaxon.klaxon.arthritis.utils.ProfilerUtil;
import klaxon.klaxon.taski.builtin.StepTask;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Cache {
    private static final String METADATA_FILE_NAME = "metadata.bin";
    private Status status;
    private String hash;
    private final Path cacheDir;

    // DashLoader metadata
    private final List<CreakModule<?>> cacheHandlers;
    private final List<CreakObjectClass<?, ?>> dashObjects;

    // Serializers
    private final RegistrySerializer registrySerializer;
    private final MappingSerializer mappingsSerializer;

    Cache(Path cacheDir, List<CreakModule<?>> cacheHandlers, List<CreakObjectClass<?, ?>> creakObjects) {
        this.cacheDir = cacheDir;
        this.cacheHandlers = cacheHandlers;
        this.dashObjects = creakObjects;
        this.registrySerializer = new RegistrySerializer(creakObjects);
        this.mappingsSerializer = new MappingSerializer(cacheHandlers);
    }

    public void start() {
        if (this.exists()) {
            this.setStatus(Cache.Status.LOAD);
            this.load();
        } else {
            this.setStatus(Cache.Status.SAVE);
        }
    }

    public boolean save(@Nullable Consumer<StepTask> taskConsumer) {
        Arthritis.LOG.info("Starting DashLoader Caching");
        try {
            if (status != Status.SAVE) {
                throw new RuntimeException("Status is not SAVE");
            }

            Path ourDir = getDir();

            // Max caches
            int maxCaches = ConfigHandler.INSTANCE.config.maxCaches;
            if (maxCaches != -1) {
                Arthritis.LOG.info("Checking for cache count.");
                try {
                    FileTime oldestTime = null;
                    Path oldestPath = null;
                    int cacheCount = 1;
                    try (Stream<Path> stream = Files.list(cacheDir)) {
                        for (Path path : stream.toList()) {
                            if (!Files.isDirectory(path)) {
                                continue;
                            }

                            if (path.equals(ourDir)) {
                                continue;
                            }
                            cacheCount += 1;

                            try {
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                FileTime lastAccessTime = attrs.lastAccessTime();
                                if (oldestTime == null || lastAccessTime.compareTo(oldestTime) < 0) {
                                    oldestTime = lastAccessTime;
                                    oldestPath = path;
                                }
                            } catch (IOException e) {
                                Arthritis.LOG.warn("Could not find access time for cache.", e);
                            }
                        }
                    }

                    if (oldestPath != null && cacheCount > maxCaches) {
                        Arthritis.LOG.info("Removing {} as we are currently above the maximum caches.", oldestPath);
                        if (!FileUtils.deleteQuietly(oldestPath.toFile())) {
                            Arthritis.LOG.error("Could not remove cache {}", oldestPath);
                        }
                    }
                } catch (IOException io) {
                    Arthritis.LOG.error("Could not enforce maximum cache ", io);
                }
            }

            long start = System.currentTimeMillis();

            StepTask main = new StepTask("save", 2);
            if (taskConsumer != null) {
                taskConsumer.accept(main);
            }

            // Setup handlers
            List<MissingHandler<?>> handlers = new ArrayList<>();
            for (CreakEntrypoint entryPoint : FabricLoader.getInstance().getEntrypoints("dashloader", CreakEntrypoint.class)) {
                entryPoint.onDashLoaderSave(handlers);
            }
            RegistryFactory factory = RegistryFactory.create(handlers, dashObjects);

            // Mappings
            mappingsSerializer.save(ourDir, factory, cacheHandlers, main);
            main.next();

            // serialization
            main.run(new StepTask("serialize", 2), (task) -> {
                try {
                    CacheInfo info = this.registrySerializer.serialize(ourDir, factory, task::setSubTask);
                    task.next();
                    Arthritis.METADATA_SERIALIZER.save(ourDir.resolve(METADATA_FILE_NAME), new StepTask("hi"), info);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                task.next();
            });

            Arthritis.LOG.info("Saved cache in " + ProfilerUtil.getTimeStringFromStart(start));
            return true;
        } catch (Throwable thr) {
            Arthritis.LOG.error("Failed caching", thr);
            this.setStatus(Status.SAVE);
            this.clear();
            return false;
        }
    }

    public void load() {
        this.status = Status.LOAD;
        long start = System.currentTimeMillis();
        try {
            StepTask task = new StepTask("Loading DashCache", 3);
            Path cacheDir = getDir();

            // Get metadata
            Path metadataPath = cacheDir.resolve(METADATA_FILE_NAME);
            CacheInfo info = Arthritis.METADATA_SERIALIZER.load(metadataPath);

            // File reading
            StageData[] stageData = registrySerializer.deserialize(cacheDir, info, dashObjects);
            RegistryReader reader = new RegistryReader(info, stageData);

            // Exporting assets
            task.run(() -> {
                reader.export(task::setSubTask);
            });

            // Loading mappings
            if (!mappingsSerializer.load(cacheDir, reader, cacheHandlers)) {
                this.setStatus(Status.SAVE);
                this.clear();
                return;
            }

            Arthritis.LOG.info("Loaded cache in {}", ProfilerUtil.getTimeStringFromStart(start));
        } catch (Exception e) {
            Arthritis.LOG.error("Summoned CrashLoader in {}", ProfilerUtil.getTimeStringFromStart(start), e);
            this.setStatus(Status.SAVE);
            this.clear();
        }
    }

    public void setHash(String hash) {
        Arthritis.LOG.info("Hash changed to " + hash);
        this.hash = hash;
    }

    public boolean exists() {
        return Files.exists(this.getDir());
    }

    public void clear() {
        try {
            FileUtils.deleteDirectory(this.getDir().toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getDir() {
        if (hash == null) {
            throw new RuntimeException("Cache hash has not been set.");
        }
        return cacheDir.resolve(hash + "/");
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
            Arthritis.LOG.info("\u001B[46m\u001B[30m DashLoader Status change {}\n\u001B[0m", status);
            this.cacheHandlers.forEach(handler -> handler.reset(this));
        }
    }

    public enum Status {
        /**
         * Idle
         */
        IDLE,
        /**
         * The cache manager is in the process of loading a cache.
         */
        LOAD,
        /**
         * The cache manager is creating a cache.
         */
        SAVE,
    }
}
