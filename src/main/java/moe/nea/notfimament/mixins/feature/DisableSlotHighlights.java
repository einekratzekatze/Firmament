package moe.nea.notfimament.mixins.feature;

import moe.nea.notfimament.features.fixes.Fixes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class DisableSlotHighlights {
	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "isHighlightable", at = @At("HEAD"), cancellable = true)
	private void dontHighlight(CallbackInfoReturnable<Boolean> cir) {
		if (!Fixes.TConfig.INSTANCE.getHideSlotHighlights()) return;
		var display = getItem().get(DataComponents.TOOLTIP_DISPLAY);
		if (display != null && display.hideTooltip())
			cir.setReturnValue(false);
	}
}
