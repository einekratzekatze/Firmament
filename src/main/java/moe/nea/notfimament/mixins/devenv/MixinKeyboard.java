

package moe.nea.notfimament.mixins.devenv;

import moe.nea.notfimament.features.debug.DeveloperFeatures;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
    @Redirect(method = "handleDebugKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;"))
    public CompletableFuture<Void> redirectReloadResources(Minecraft instance) {
        return DeveloperFeatures.hookOnBeforeResourceReload(instance);
    }
}
