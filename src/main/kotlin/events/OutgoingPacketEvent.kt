

package moe.nea.notfimament.events

import net.minecraft.network.protocol.Packet

data class OutgoingPacketEvent(val packet: Packet<*>) : FirmamentEvent.Cancellable() {
    companion object : FirmamentEventBus<OutgoingPacketEvent>()
}
