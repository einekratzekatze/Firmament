
package moe.nea.notfimament.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import moe.nea.notfimament.events.SlotClickEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class SlotClickEventPatch {

    @Inject(method = "handleInventoryMouseClick", at = @At(value = "FIELD", target =
		"Lnet/minecraft/world/inventory/AbstractContainerMenu;slots:Lnet/minecraft/core/NonNullList;", opcode = Opcodes.GETFIELD))
    private void onSlotClickSaveSlot(int containerId, int slotId, int mouseButton, ClickType clickType, Player player, CallbackInfo ci, @Local AbstractContainerMenu handler, @Share("slotContent") LocalRef<ItemStack> slotContent) {
        if (0 <= slotId && slotId < handler.slots.size()) {
            slotContent.set(handler.getSlot(slotId).getItem().copy());
        }
    }

    @Inject(method = "handleInventoryMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void onSlotClick(int syncId, int slotId, int button, ClickType actionType, Player player, CallbackInfo ci, @Local AbstractContainerMenu handler, @Share("slotContent") LocalRef<ItemStack> slotContent) {
        if (0 <= slotId && slotId < handler.slots.size()) {
            SlotClickEvent.Companion.publish(new SlotClickEvent(
                handler.getSlot(slotId),
                slotContent.get(),
                button,
                actionType
            ));
        }
    }
}
