package klaxon.klaxon.arthritis.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import klaxon.klaxon.arthritis.Arthritis;
import net.minecraft.client.main.Main;

@Mixin(Main.class)
public class MainMixin {
	private static boolean INITIALIZED = false;

	@Inject(
			method = "main*",
			at = @At(value = "HEAD")
	)
	private static void main(String[] args, CallbackInfo ci) {
		if (!INITIALIZED) {
			Arthritis.bootstrap();
			INITIALIZED = true;
		}
	}
}
