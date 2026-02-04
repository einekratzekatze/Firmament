
package moe.nea.notfimament.mixins.devenv;

import moe.nea.notfimament.Firmament;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiscardedPayload.class)
public class WarnForUnknownCustomPayloadSends {
    @Inject(method = "method_56493", at = @At("HEAD"))
    private static void warn(DiscardedPayload value, FriendlyByteBuf buf, CallbackInfo ci) {
        Firmament.INSTANCE.getLogger().warn("Unknown custom payload is being sent: {}", value);
    }
}
