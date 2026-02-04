

package moe.nea.notfimament.mixins;

import moe.nea.notfimament.features.fixes.Fixes;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class ToggleSprintPatch {
    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    public void onIsPressed(CallbackInfoReturnable<Boolean> cir) {
        Fixes.INSTANCE.handleIsPressed((KeyMapping) (Object) this, cir);
    }
}
