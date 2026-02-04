package moe.nea.notfimament.mixins.devenv;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TranslatableContents.class)
public abstract class EarlyInstantiateTranslations {
	@Shadow
	protected abstract void decompose();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(String key, String fallback, Object[] args, CallbackInfo ci) {
		decompose();
	}
}
