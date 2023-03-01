package klaxon.klaxon.arthritis.client;

import klaxon.klaxon.arthritis.Cache;
import klaxon.klaxon.arthritis.CacheFactory;

import java.nio.file.Path;
import java.util.List;

public class ArthritisClient {

    public static final Cache CACHE;

    public static boolean NEEDS_RELOAD = false;

    static {
        CacheFactory cacheManagerFactory = new CacheFactory();
        List<DashEntrypoint> entryPoints = FabricLoader.getInstance().getEntrypoints("dashloader", DashEntrypoint.class);
        for (DashEntrypoint entryPoint : entryPoints) {
            entryPoint.onDashLoaderInit(cacheManagerFactory);
        }

        CACHE = cacheManagerFactory.build(Path.of("./dashloader-cache/client/"));
    }
}
