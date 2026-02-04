

package moe.nea.notfimament.mixins;

import kotlin.Pair;
import moe.nea.notfimament.features.inventory.SaveCursorPosition;
import net.minecraft.client.MouseHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class SaveCursorPositionPatch {
	@Shadow
	private double xpos;

	@Shadow
	private double ypos;

	@Inject(method = "grabMouse", at = @At(value = "HEAD"))
	public void onLockCursor(CallbackInfo ci) {
		SaveCursorPosition.saveCursorOriginal(xpos, ypos);
	}

	@Inject(method = "grabMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 2))
	public void onLockCursorAfter(CallbackInfo ci) {
		SaveCursorPosition.saveCursorMiddle(xpos, ypos);
	}

	@Inject(method = "releaseMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 2))
	public void onUnlockCursor(CallbackInfo ci) {
		Pair<Double, Double> cursorPosition = SaveCursorPosition.loadCursor(this.xpos, this.ypos);
		if (cursorPosition == null) return;
		this.xpos = cursorPosition.getFirst();
		this.ypos = cursorPosition.getSecond();
	}
}
