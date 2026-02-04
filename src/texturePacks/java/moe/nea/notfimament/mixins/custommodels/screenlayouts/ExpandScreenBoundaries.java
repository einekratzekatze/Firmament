package moe.nea.notfimament.mixins.custommodels.screenlayouts;

import moe.nea.notfimament.features.texturepack.CustomScreenLayouts;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractContainerScreen.class, AbstractRecipeBookScreen.class})
public class ExpandScreenBoundaries {
	@Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
	private void onClickOutsideBounds(double mouseX, double mouseY, int left, int top, CallbackInfoReturnable<Boolean> cir) {
		var background = CustomScreenLayouts.getMover(CustomScreenLayouts.CustomScreenLayout::getBackground);
		if (background == null) return;
		var x = background.getX() + left;
		var y = background.getY() + top;
		cir.setReturnValue(mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + background.getWidth()) || mouseY >= (double) (y + background.getHeight()));
	}
}
