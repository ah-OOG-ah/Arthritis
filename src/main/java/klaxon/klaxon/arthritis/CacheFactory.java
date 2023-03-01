package klaxon.klaxon.arthritis;

import klaxon.klaxon.arthritis.api.CreakModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CacheFactory {
    private static final Logger LOGGER = LogManager.getLogger("CacherFactory");
    private final List<CreakObjectClass<?, ?>> creakObjects;
    private final List<CreakModule<?>> cacheHandlers;
    private boolean failed = false;

    public CacheFactory() {
        this.creakObjects = new ArrayList<>();
        this.cacheHandlers = new ArrayList<>();
    }

    public void addDashObject(Class<?> dashClass) {
        final Class<?>[] interfaces = dashClass.getInterfaces();
        if (interfaces.length == 0) {
            LOGGER.error("No DashObject interface found. Class: {}", dashClass.getSimpleName());
            this.failed = true;
            return;
        }
        this.creakObjects.add(new CreakObjectClass<>(dashClass));
    }

    public void addCacheHandler(CreakModule<?> handler) {
        this.cacheHandlers.add(handler);
    }

    public Cache build(Path cacheDir) {
        if (this.failed) {
            throw new RuntimeException("Failed to initialize the API");
        }

        // Set dashobject ids
        this.creakObjects.sort(Comparator.comparing(o -> o.getDashClass().getName()));
        this.cacheHandlers.sort(Comparator.comparing(o -> o.getDataClass().getName()));
        List<CreakObjectClass<?, ?>> objects = this.creakObjects;
        for (int i = 0; i < objects.size(); i++) {
            CreakObjectClass<?, ?> dashObject = objects.get(i);
            dashObject.dashObjectId = i;
        }

        return new Cache(cacheDir.resolve(Arthritis.MOD_HASH + "/"), cacheHandlers, creakObjects);

    }
}
