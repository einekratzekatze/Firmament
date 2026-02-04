package moe.nea.notfimament.mixins.custommodels.screenlayouts;

import moe.nea.notfimament.features.texturepack.CustomScreenLayouts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CraftingScreen.class, CrafterScreen.class, DispenserScreen.class, ContainerScreen.class, HopperScreen.class, ShulkerBoxScreen.class,})
public abstract class ReplaceGenericBackgrounds extends AbstractContainerScreen<AbstractContainerMenu> {
	// TODO: split out screens with special background components like flames, arrows, etc. (maybe arrows deserve generic handling tho)
	public ReplaceGenericBackgrounds(AbstractContainerMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
	private void replaceDrawBackground(GuiGraphics context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
		final var override = CustomScreenLayouts.getActiveScreenOverride();
		if (override == null || override.getBackground() == null) return;
		override.getBackground().renderGeneric(context, this);
		ci.cancel();
	}
}
