package moe.nea.notfimament.mixins.custommodels;

import moe.nea.notfimament.features.texturepack.CustomBlockTextures;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class ReplaceFallbackBlockModel {
    // TODO: add check to BlockDustParticle
    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    private void getModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        var replacement = CustomBlockTextures.getReplacementModel(state, null);
        if (replacement != null)
            cir.setReturnValue(replacement);
    }
}
