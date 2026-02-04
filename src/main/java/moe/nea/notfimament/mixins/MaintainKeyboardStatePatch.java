package moe.nea.notfimament.mixins;

import moe.nea.notfimament.keybindings.FirmamentKeyboardState;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MaintainKeyboardStatePatch {
	@Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/FramerateLimitTracker;onInputReceived()V"))
	private void onKeyInput(long window, int action, KeyEvent input, CallbackInfo ci) {
		FirmamentKeyboardState.INSTANCE.maintainState(input, action);
	}
}
