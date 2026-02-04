package moe.nea.notfimament.mixins;

import moe.nea.notfimament.features.chat.QuickCommands;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class SaveOriginalCommandTreePacket {
	@Inject(method = "handleCommands", at = @At(value = "RETURN"))
	private void saveUnmodifiedCommandTree(ClientboundCommandsPacket packet, CallbackInfo ci) {
		QuickCommands.INSTANCE.setLastReceivedTreePacket(packet);
	}
}
