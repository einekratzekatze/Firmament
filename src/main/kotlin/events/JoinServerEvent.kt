package moe.nea.notfimament.events

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.multiplayer.ClientPacketListener

data class JoinServerEvent(
    val networkHandler: ClientPacketListener,
    val packetSender: PacketSender,
) : FirmamentEvent() {
	companion object : FirmamentEventBus<JoinServerEvent>()
}
