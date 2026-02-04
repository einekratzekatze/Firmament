package moe.nea.notfimament.mixins.custommodels;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.features.texturepack.CustomBlockTextures;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelBakery.class)
public class BuildExtraBlockStateModels {
	@ModifyReturnValue(method = "bakeModels", at = @At("RETURN"))
	private CompletableFuture<ModelBakery.BakingResult> injectMoreBlockModels(CompletableFuture<ModelBakery.BakingResult> original, @Local ModelBakery.ModelBakerImpl baker, @Local(argsOnly = true) Executor executor) {
		ModelBaker b = baker;
		return original.thenCombine(
			CustomBlockTextures.createBakedModels(b, executor),
			(a, _void) -> a
		);
	}
}
