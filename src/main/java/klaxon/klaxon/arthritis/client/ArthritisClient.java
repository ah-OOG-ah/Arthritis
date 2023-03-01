package klaxon.klaxon.arthritis.client;

import klaxon.klaxon.arthritis.Cache;
import klaxon.klaxon.arthritis.CacheFactory;
import klaxon.klaxon.arthritis.api.CreakEntrypoint;
import klaxon.klaxon.arthritis.api.MissingHandler;

import java.nio.file.Path;
import java.util.List;

public class ArthritisClient implements CreakEntrypoint {

    public static final Cache CACHE;

    public static boolean NEEDS_RELOAD = false;

    static {
        CacheFactory cacheManagerFactory = new CacheFactory();
        // TODO: Fix this mess!
        // Here lies the first roadblock: the Fabric Loader has an API for fetching "entrypoints", which appear to
        // just be implementations of an entrypoint class. Forge doesn't have anything that loos similar.
        // For now, I'm only loading our implementation.
        /*List<CreakEntrypoint> entryPoints = FabricLoader.getInstance().getEntrypoints("dashloader", CreakEntrypoint.class);
        for (CreakEntrypoint entryPoint : entryPoints) {
            entryPoint.onDashLoaderInit(cacheManagerFactory);
        }*/
        ArthritisClient.onDashLoaderSave(handlers);

        CACHE = cacheManagerFactory.build(Path.of("./dashloader-cache/client/"));
    }

    @Override
    public void onDashLoaderInit(CacheFactory factory) {
        factory.addCacheHandler(new FontModule());
        factory.addCacheHandler(new ModelModule());
        factory.addCacheHandler(new ShaderModule());
        factory.addCacheHandler(new SplashModule());
        factory.addCacheHandler(new SpriteModule());

        for (Class<?> aClass : new Class[]{
            DashIdentifier.class,
            DashModelIdentifier.class,
            DashBasicBakedModel.class,
            DashBuiltinBakedModel.class,
            DashMultipartBakedModel.class,
            DashWeightedBakedModel.class,
            DashBakedQuad.class,
            DashBakedQuadCollection.class,
            DashAndPredicate.class,
            DashOrPredicate.class,
            DashSimplePredicate.class,
            DashStaticPredicate.class,
            DashImage.class,
            DashSprite.class,
            DashBitmapFont.class,
            DashBlankFont.class,
            DashSpaceFont.class,
            DashTrueTypeFont.class,
            DashUnicodeFont.class,
            DashBlockState.class,
            DashVertexFormat.class,
            DashShader.class
        }) {
            factory.addDashObject(aClass);
        }
    }

    @Override
    public void onDashLoaderSave(List<MissingHandler<?>> handlers) {
        handlers.add(new MissingHandler<>(
            Identifier.class, // TODO: Second major roadblock: 1.7.10 doesn't have this class
            (identifier, registryWriter) -> {
                if (identifier instanceof ModelIdentifier m) {
                    return new DashModelIdentifier(m);
                } else {
                    return new DashIdentifier(identifier);
                }
            }
        ));
        handlers.add(new MissingHandler<>(
            MultipartModelSelector.class,
            (selector, writer) -> {
                if (selector == MultipartModelSelector.TRUE) {
                    return new DashStaticPredicate(true);
                } else if (selector == MultipartModelSelector.FALSE) {
                    return new DashStaticPredicate(false);
                } else if (selector instanceof AndMultipartModelSelector s) {
                    return new DashAndPredicate(s, writer);
                } else if (selector instanceof OrMultipartModelSelector s) {
                    return new DashOrPredicate(s, writer);
                } else if (selector instanceof SimpleMultipartModelSelector s) {
                    return new DashSimplePredicate(s);
                } else if (selector instanceof BooleanSelector s) {
                    return new DashStaticPredicate(s.selector);
                } else {
                    throw new RuntimeException("someone is having fun with lambda selectors again");
                }
            }
        ));
    }
}
