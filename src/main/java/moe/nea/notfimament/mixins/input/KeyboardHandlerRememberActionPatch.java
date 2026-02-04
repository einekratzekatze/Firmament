package moe.nea.notfimament.mixins.input;

import moe.nea.notfimament.events.HandledScreenKeyPressedEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerRememberActionPatch {
	@Inject(method = "keyPress", at = @At("HEAD"))
	private static void saveActionOnKeyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
		HandledScreenKeyPressedEvent.Companion.internalPushAction(action);
	}
}
