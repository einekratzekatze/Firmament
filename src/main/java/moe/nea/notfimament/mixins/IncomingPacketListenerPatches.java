

package moe.nea.notfimament.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.CommandDispatcher;
import moe.nea.notfimament.events.MaskCommands;
import moe.nea.notfimament.events.ParticleSpawnEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class IncomingPacketListenerPatches {

    @ModifyExpressionValue(method = "handleCommands", at = @At(value = "NEW", target = "(Lcom/mojang/brigadier/tree/RootCommandNode;)Lcom/mojang/brigadier/CommandDispatcher;", remap = false))
    public CommandDispatcher onOnCommandTree(CommandDispatcher dispatcher) {
        MaskCommands.Companion.publish(new MaskCommands(dispatcher));
        return dispatcher;
    }

    @Inject(method = "handleParticleEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V", shift = At.Shift.AFTER), cancellable = true)
    public void onParticleSpawn(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        var event = new ParticleSpawnEvent(
            packet.getParticle(),
            new Vec3(packet.getX(), packet.getY(), packet.getZ()),
            new Vector3f(packet.getXDist(), packet.getYDist(), packet.getZDist()),
            packet.alwaysShow(),
            packet.getCount(),
            packet.getMaxSpeed()
        );
        ParticleSpawnEvent.Companion.publish(event);
        if (event.getCancelled())
            ci.cancel();
    }
}
