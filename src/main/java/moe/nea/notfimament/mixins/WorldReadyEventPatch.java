

package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.WorldReadyEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class WorldReadyEventPatch {
	@Inject(method = "setLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;)V", shift = At.Shift.AFTER))
	public void onClose(CallbackInfo ci) {
		WorldReadyEvent.Companion.publish(new WorldReadyEvent());
	}
}
