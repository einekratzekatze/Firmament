

package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.IsSlotProtectedEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class PlayerDropEventPatch extends Player {
	public PlayerDropEventPatch() {
		super(null, null);
	}

	@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
	public void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
		Slot fakeSlot = new Slot(getInventory(), getInventory().getSelectedSlot(), 0, 0);
		if (IsSlotProtectedEvent.shouldBlockInteraction(fakeSlot, ClickType.THROW, IsSlotProtectedEvent.MoveOrigin.DROP_FROM_HOTBAR)) {
			cir.setReturnValue(false);
		}
	}
}
