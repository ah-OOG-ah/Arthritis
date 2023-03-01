package klaxon.klaxon.arthritis.api;

import klaxon.klaxon.arthritis.Cache;
import klaxon.klaxon.arthritis.registry.RegistryFactory;
import klaxon.klaxon.arthritis.registry.RegistryReader;
import klaxon.klaxon.taski.builtin.StepTask;

public interface CreakModule<M> {
    void reset(Cache cacheManager);

    M save(RegistryFactory writer, StepTask task);

    void load(M mappings, RegistryReader reader, StepTask task);

    Class<M> getDataClass();

    boolean isActive();

    default float taskWeight() {
        return 100;
    }
}
