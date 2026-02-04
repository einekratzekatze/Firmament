package moe.nea.notfimament.mixins.sodium.custommodels;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.features.texturepack.CustomBlockTextures;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilderMeshingTask.class)
public class PatchBlockModelInSodiumChunkGenerator {
	@WrapOperation(
		method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;"))
	private BlockStateModel replaceBlockModel(BlockModelShaper instance, BlockState state, Operation<BlockStateModel> original,
                                              @Local(name = "blockPos") BlockPos.MutableBlockPos pos) {
		var replacement = CustomBlockTextures.getReplacementModel(state, pos);
		if (replacement != null) return replacement;
		CustomBlockTextures.enterFallbackCall();
		var fallback = original.call(instance, state);
		CustomBlockTextures.exitFallbackCall();
		return fallback;
	}
}
