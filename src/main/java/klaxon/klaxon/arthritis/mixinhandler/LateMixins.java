package klaxon.klaxon.arthritis.mixinhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

// Most this is shamelessly copied from Hodgepodge, thank you, Mitch!
@LateMixin
public class LateMixins implements ILateMixinLoader {

    // This class SHOULD load late mixins.
    // GTNHMixins should load it, GO does not reference this class.

    // Returns the LateMixin config JSON
    @Override
    public String getMixinConfig() {
        return "mixins.arthritis.late.json";
    }

    // Returns the actual mixins to load
    @Override
    public List<String> getMixins(Set<String> loadedMods) {

        // List of mixins
        final List<String> mixins = new ArrayList<>();

        // For each mixin...
        for (Mixins mixin : Mixins.values()) {

            // If late
            if (mixin.phase == Mixins.Phase.LATE) {

                // Add them to mixins
                mixins.addAll(mixin.mixinClasses);
            }
        }

        // Return a list of mixins!
        return mixins;
    }
}
