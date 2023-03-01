package klaxon.klaxon.arthritis.registry.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import klaxon.klaxon.arthritis.CreakObjectClass;
import klaxon.klaxon.arthritis.api.CreakObject;
import klaxon.klaxon.arthritis.registry.RegistryFactory;
import klaxon.klaxon.arthritis.registry.RegistryWriter;

import java.util.ArrayList;
import java.util.List;

public class ChunkFactory<R, D extends CreakObject<R>> {
    public final byte chunkId;
    public final String name;
    public final CreakObjectClass<R, D> dashObject;
    public final List<Entry<D>> list = new ArrayList<>();
    public final Object2IntMap<D> deduplication = new Object2IntOpenHashMap<>();
    private final FactoryBinding<R, D> factory;

    public ChunkFactory(byte chunkId, String name, FactoryBinding<R, D> factory, CreakObjectClass<R, D> dashObject) {
        this.chunkId = chunkId;
        this.name = name;
        this.factory = factory;
        this.dashObject = dashObject;
    }

    public D create(R raw, RegistryWriter writer) {
        return this.factory.create(raw, writer);
    }

    public int add(Entry<D> entry, RegistryFactory factory) {
        int existing = deduplication.getOrDefault(entry.data, -1);
        if (existing != -1) {
            return RegistryUtil.createId(existing, chunkId);
        }

        final int pos = this.list.size();
        this.list.add(entry);

        // Add to deduplication
        deduplication.put(entry.data, pos);

        // Increment dependencies
        for (int dependency : entry.dependencies) {
            ChunkFactory<?, ?> chunk = factory.chunks[RegistryUtil.getChunkId(dependency)];
            ChunkFactory.Entry<?> dependencyEntry = chunk.list.get(RegistryUtil.getObjectId(dependency));
            dependencyEntry.references++;
        }

        return RegistryUtil.createId(pos, chunkId);
    }

    public static final class Entry<D> {
        public final D data;
        public final int[] dependencies;
        public int references = 0;
        public int stage = -1;

        public Entry(D data, int[] dependencies) {
            this.data = data;
            this.dependencies = dependencies;
        }
    }
}
