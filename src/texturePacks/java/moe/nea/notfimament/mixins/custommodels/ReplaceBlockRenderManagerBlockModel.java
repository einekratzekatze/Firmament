package moe.nea.notfimament.mixins.custommodels;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.features.texturepack.CustomBlockTextures;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public class ReplaceBlockRenderManagerBlockModel {
	@WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;"))
	private BlockStateModel replaceModelInRenderBlock(BlockRenderDispatcher instance, BlockState state, Operation<BlockStateModel> original, @Local(ordinal = 2) BlockPos pos) {
		var replacement = CustomBlockTextures.getReplacementModel(state, pos);
		if (replacement != null) return replacement;
		CustomBlockTextures.enterFallbackCall();
		var fallback = original.call(instance, state);
		CustomBlockTextures.exitFallbackCall();
		return fallback;
	}
//TODO: cover renderDamage model
//    @WrapOperation(method = "renderDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockModels;getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"))
//    private BakedModel replaceModelInRenderDamage(
//        BlockModels instance, BlockState state, Operation<BakedModel> original, @Local(argsOnly = true) BlockPos pos) {
//        var replacement = CustomBlockTextures.getReplacementModel(state, pos);
//        if (replacement != null) return replacement;
//        CustomBlockTextures.enterFallbackCall();
//        var fallback = original.call(instance, state);
//        CustomBlockTextures.exitFallbackCall();
//        return fallback;
//    }
}
