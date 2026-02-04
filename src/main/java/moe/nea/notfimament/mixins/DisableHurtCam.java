package moe.nea.notfimament.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.nea.notfimament.features.fixes.Fixes;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class DisableHurtCam {
	@ModifyExpressionValue(method = "bobHurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", opcode = Opcodes.GETFIELD))
	private int replaceHurtTime(int original) {
		if (Fixes.TConfig.INSTANCE.getNoHurtCam())
			return 0;
		return original;
	}
}
