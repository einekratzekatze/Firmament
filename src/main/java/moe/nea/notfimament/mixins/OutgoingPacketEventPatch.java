

package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.OutgoingPacketEvent;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class OutgoingPacketEventPatch {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (OutgoingPacketEvent.Companion.publish(new OutgoingPacketEvent(packet)).getCancelled()) {
            ci.cancel();
        }
    }
}
