package klaxon.klaxon.arthritis.mixins.early;

import klaxon.klaxon.arthritis.Arthritis;
import klaxon.klaxon.arthritis.utils.ProfilerUtil;

import net.minecraft.init.Bootstrap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Bootstrap.class)
public class BootstrapMixin {

    private static long BOOTSTRAP_START = -1;

    @Inject(method = "func_151354_b", at = @At("HEAD"), remap = false)
    private static void timeStart(CallbackInfo ci) {

        BOOTSTRAP_START = System.currentTimeMillis();
    }

    @Inject(method = "func_151354_b", at = @At("TAIL"), remap = false)
    private static void timeStop(CallbackInfo ci) {

        Arthritis.LOG.info("Minecraft bootstrap in {}", ProfilerUtil.getTimeStringFromStart(BOOTSTRAP_START));
    }
}
