package moe.nea.notfimament.mixins.custommodels.screenlayouts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.nea.notfimament.features.texturepack.CustomScreenLayouts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractContainerScreen.class)
// TODO: MerchantScreen.class, BeaconScreen.class
public class ReplaceTextColorInHandledScreen {

	@WrapOperation(
		method = "renderLabels",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"),
		slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;title:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETFIELD),
			to = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;playerInventoryTitle:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETFIELD)
		),
		allow = 1,
		require = 1)
	private void replaceContainerTitle(GuiGraphics instance, Font textRenderer, Component text, int x, int y, int color, boolean shadow, Operation<Void> original) {
		var textOverride = CustomScreenLayouts.getTextMover(CustomScreenLayouts.CustomScreenLayout::getContainerTitle);
		 original.call(instance, textRenderer,
			textOverride.replaceText(text),
			textOverride.replaceX(textRenderer, text, x),
			textOverride.replaceY(y),
			textOverride.replaceColor(text, color),
			shadow);
	}

	@WrapOperation(
		method = "renderLabels",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"),
		slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;playerInventoryTitle:Lnet/minecraft/network/chat/Component;", opcode = Opcodes.GETFIELD),
			to = @At(value = "TAIL")
		),
		allow = 1,
		require = 1)
	private void replacePlayerTitle(GuiGraphics instance, Font textRenderer, Component text, int x, int y, int color, boolean shadow, Operation<Void> original) {
		var textOverride = CustomScreenLayouts.getTextMover(CustomScreenLayouts.CustomScreenLayout::getPlayerTitle);
		original.call(instance, textRenderer,
			textOverride.replaceText(text),
			textOverride.replaceX(textRenderer, text, x),
			textOverride.replaceY(y),
			textOverride.replaceColor(text, color),
			shadow);
	}
}
