package klaxon.klaxon.arthritis;

import cpw.mods.fml.common.*;
import klaxon.klaxon.arthritis.io.Serializer;
import klaxon.klaxon.arthritis.io.data.CacheInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

import java.util.ArrayList;
import java.util.Comparator;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class Arthritis {

    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    public static final Serializer<CacheInfo> METADATA_SERIALIZER = new Serializer<>(CacheInfo.class);

    public static final String MOD_HASH;

    @SidedProxy(clientSide = "klaxon.klaxon.arthritis.ClientProxy", serverSide = "klaxon.klaxon.arthritis.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    static {
        ArrayList<ModMetadata> versions = new ArrayList<>();
        for (ModContainer mod : Loader.instance().getModList()) {
            ModMetadata metadata = mod.getMetadata();
            versions.add(metadata);
        }

        versions.sort(Comparator.comparing(modMetadata -> modMetadata.modId));

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < versions.size(); i++) {
            ModMetadata metadata = versions.get(i);
            stringBuilder.append(i).append("$").append(metadata.modId).append('&').append(metadata.version);
        }

        MOD_HASH = DigestUtils.md5Hex(stringBuilder.toString()).toUpperCase();
    }

    @SuppressWarnings("EmptyMethod")
    public static void bootstrap() {
    }
}
