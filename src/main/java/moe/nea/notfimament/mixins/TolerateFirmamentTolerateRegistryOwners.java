package moe.nea.notfimament.mixins;

import moe.nea.notfimament.util.mc.TolerantRegistriesOps;
import net.minecraft.core.HolderOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HolderOwner.class)
public interface TolerateFirmamentTolerateRegistryOwners<T> {
	@Inject(method = "canSerializeIn", at = @At("HEAD"), cancellable = true)
	private void equalTolerantRegistryOwners(HolderOwner<T> other, CallbackInfoReturnable<Boolean> cir) {
		if (other instanceof TolerantRegistriesOps.TolerantOwner<?>) {
			cir.setReturnValue(true);
		}
	}
}
