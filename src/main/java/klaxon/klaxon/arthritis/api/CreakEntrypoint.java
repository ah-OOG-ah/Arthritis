package klaxon.klaxon.arthritis.api;

import klaxon.klaxon.arthritis.CacheFactory;

import java.util.List;

public interface CreakEntrypoint {
    void onDashLoaderInit(CacheFactory factory);

    void onDashLoaderSave(List<MissingHandler<?>> handlers);
}
