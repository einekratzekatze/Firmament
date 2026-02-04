

package moe.nea.notfimament.mixins.devenv;

import net.minecraft.world.entity.projectile.FishingHook;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public class DisableInvalidFishingHook {
    @Redirect(method = "recreateFromPacket", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    public void onOnSpawnPacket(Logger instance, String s, Object o, Object o1) {
        // Don't warn for broken fishing hooks, since HyPixel sends a bunch of those
    }
}
