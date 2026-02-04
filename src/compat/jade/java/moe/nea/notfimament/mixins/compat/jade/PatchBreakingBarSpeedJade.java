package moe.nea.notfimament.mixins.compat.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.nea.notfimament.compat.jade.CustomMiningHardnessProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.JadeClient;

@Mixin(JadeClient.class)
public class PatchBreakingBarSpeedJade {
	@ModifyExpressionValue(
		method = "drawBreakingProgress",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyProgress:F", opcode = Opcodes.GETFIELD)
	)
	private static float replaceBlockBreakingProgress(float original) {
		return CustomMiningHardnessProvider.replaceBreakProgress(original);
	}

	@ModifyExpressionValue(method = "drawBreakingProgress",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
	private static float replacePlayerSpecificBreakingProgress(float original) {
		return CustomMiningHardnessProvider.replaceBlockBreakSpeed(original);
	}
}
