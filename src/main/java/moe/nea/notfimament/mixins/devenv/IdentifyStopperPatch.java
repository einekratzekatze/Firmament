
package moe.nea.notfimament.mixins.devenv;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class IdentifyStopperPatch {
	@Shadow
	private volatile boolean running;

	@Inject(method = "stop", at = @At("HEAD"))
	private void onStop(CallbackInfo ci) {
		if (this.running)
			Thread.dumpStack();
	}
}
