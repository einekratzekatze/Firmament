package moe.nea.notfimament.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import moe.nea.notfimament.events.WorldMouseMoveEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class DispatchMouseInputEventsPatch {
	@WrapWithCondition(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public boolean onRotatePlayer(LocalPlayer instance, double deltaX, double deltaY) {
		var event = WorldMouseMoveEvent.Companion.publish(new WorldMouseMoveEvent(deltaX, deltaY));
		return !event.getCancelled();
	}
}
