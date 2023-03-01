package klaxon.klaxon.arthritis.mixins.early;

import klaxon.klaxon.arthritis.client.ArthritisClient;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "refreshResources",
        at = @At(value = "HEAD"))
    private void requestReload(CallbackInfo ci) {
        ArthritisClient.NEEDS_RELOAD = true;
    }


    @Inject(method = "refreshResources", at = @At(value = "RETURN"))
    private void reloadComplete(CallbackInfo ci) {
        // Reset the manager
        if (ArthritisClient.CACHE.getStatus() == Cache.Status.LOAD) {
            ArthritisClient.CACHE.setStatus(Cache.Status.IDLE);
        }
    }
}
