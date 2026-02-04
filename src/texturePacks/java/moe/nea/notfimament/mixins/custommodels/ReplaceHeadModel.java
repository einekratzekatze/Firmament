package moe.nea.notfimament.mixins.custommodels;

import moe.nea.notfimament.features.texturepack.HeadModelChooser;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class ReplaceHeadModel<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
	@Shadow
	@Final
	protected ItemModelResolver itemModelResolver;

	@Unique
	private ItemStackRenderState tempRenderState = new ItemStackRenderState();

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
		at = @At("TAIL")
	)
	private void replaceHeadModel(
		T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci
	) {
		var headItemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);

		HeadModelChooser.INSTANCE.getIS_CHOOSING_HEAD_MODEL().set(true);
		tempRenderState.clear();
		this.itemModelResolver.updateForLiving(tempRenderState, headItemStack, ItemDisplayContext.HEAD, livingEntity);
		HeadModelChooser.INSTANCE.getIS_CHOOSING_HEAD_MODEL().set(false);

		if (HeadModelChooser.HasExplicitHeadModelMarker.cast(tempRenderState)
			.isExplicitHeadModel_Firmament()) {
			livingEntityRenderState.wornHeadType = null;
			var temp = livingEntityRenderState.headItem;
			livingEntityRenderState.headItem = tempRenderState;
			tempRenderState = temp;
		}
	}
}
