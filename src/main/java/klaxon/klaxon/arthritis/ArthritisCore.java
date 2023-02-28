package klaxon.klaxon.arthritis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import klaxon.klaxon.arthritis.mixinhandler.Mixins;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002)
public class ArthritisCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final SortingIndex index = ArthritisCore.class.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);

    public static int getSortingIndex() {
        return index != null ? index.value() : 0;
    }

    // EarlyMixin boilerplate
    @Override
    public String getMixinConfig() {
        return "mixins.arthritis.early.json";
    }

    // Ditto
    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {

        // All mixins
        final List<String> mixins = new ArrayList<>();
        // The ones we won't invite to Thanksgiving
        final List<String> notLoading = new ArrayList<>();

        // For every mixin we could load...
        for (Mixins mixin : Mixins.values()) {

            // Pick the early ones
            if (mixin.phase == Mixins.Phase.EARLY) {

                // Load if necessary
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }

        // Log what we don't do
        Arthritis.LOG.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
