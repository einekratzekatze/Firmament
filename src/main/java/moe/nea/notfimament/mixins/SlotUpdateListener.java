
package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.ChestInventoryUpdateEvent;
import moe.nea.notfimament.events.PlayerInventoryUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class SlotUpdateListener extends ClientCommonPacketListenerImpl {
	protected SlotUpdateListener(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(
		method = "handleContainerSetSlot",
		at = @At(value = "TAIL"))
	private void onSingleSlotUpdate(
		ClientboundContainerSetSlotPacket packet,
		CallbackInfo ci) {
		var player = this.minecraft.player;
		assert player != null;
		if (packet.getContainerId() == 0) {
			PlayerInventoryUpdate.Companion.publish(new PlayerInventoryUpdate.Single(packet.getSlot(), packet.getItem()));
		} else if (packet.getContainerId() == player.containerMenu.containerId) {
			ChestInventoryUpdateEvent.Companion.publish(
				new ChestInventoryUpdateEvent.Single(packet.getSlot(), packet.getItem())
			);
		}
	}

	@Inject(method = "handleContainerContent",
		at = @At("TAIL"))
	private void onMultiSlotUpdate(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
		var player = this.minecraft.player;
		assert player != null;
		if (packet.containerId() == 0) {
			PlayerInventoryUpdate.Companion.publish(new PlayerInventoryUpdate.Multi(packet.items()));
		} else if (packet.containerId() == player.containerMenu.containerId) {
			ChestInventoryUpdateEvent.Companion.publish(
				new ChestInventoryUpdateEvent.Multi(packet.items())
			);
		}
	}
}
