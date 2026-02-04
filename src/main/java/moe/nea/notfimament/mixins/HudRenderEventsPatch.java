

package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.HotbarItemRenderEvent;
import moe.nea.notfimament.events.HudRenderEvent;
import moe.nea.notfimament.features.fixes.Fixes;
import moe.nea.notfimament.util.SBData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HudRenderEventsPatch {
    @Inject(method = "renderSleepOverlay", at = @At(value = "HEAD"))
    public void renderCallBack(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        HudRenderEvent.Companion.publish(new HudRenderEvent(context, tickCounter));
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void onRenderHotbarItem(GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        if (stack != null && !stack.isEmpty())
            HotbarItemRenderEvent.Companion.publish(new HotbarItemRenderEvent(stack, context, x, y, tickCounter));
    }

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	public void hideStatusEffects(CallbackInfo ci) {
		if (Fixes.TConfig.INSTANCE.getHidePotionEffectsHud() && SBData.INSTANCE.isOnSkyblock()) ci.cancel();
	}

}
