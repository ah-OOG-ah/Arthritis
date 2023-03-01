package klaxon.klaxon.arthritis.api;

import klaxon.klaxon.arthritis.Cache;

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
