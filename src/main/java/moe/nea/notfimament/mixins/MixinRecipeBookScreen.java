package moe.nea.notfimament.mixins;

import moe.nea.notfimament.features.fixes.Fixes;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractRecipeBookScreen.class, priority = 999)
public class MixinRecipeBookScreen {
	@Inject(method = "initButton", at = @At("HEAD"), cancellable = true)
	public void addRecipeBook(CallbackInfo ci) {
		if (Fixes.TConfig.INSTANCE.getHideRecipeBook()) ci.cancel();
	}
}
