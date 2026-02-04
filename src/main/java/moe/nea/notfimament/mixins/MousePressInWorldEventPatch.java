package moe.nea.notfimament.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.events.WorldKeyboardEvent;
import moe.nea.notfimament.keybindings.GenericInputAction;
import moe.nea.notfimament.keybindings.InputModifiers;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MousePressInWorldEventPatch {
	@WrapWithCondition(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	public boolean onKeyBoardInWorld(InputConstants.Key key, @Local(argsOnly = true) MouseButtonInfo input) { // TODO: handle modified mouse click instead
		var event = WorldKeyboardEvent.Companion.publish(new WorldKeyboardEvent(GenericInputAction.of(input),
			InputModifiers.of(input)));
		return !event.getCancelled();
	}
}
