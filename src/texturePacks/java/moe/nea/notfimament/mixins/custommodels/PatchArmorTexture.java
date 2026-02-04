
package moe.nea.notfimament.mixins.custommodels;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.features.texturepack.CustomGlobalArmorOverrides;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class PatchArmorTexture {
	@ModifyExpressionValue(
		method = "renderArmorPiece",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
	private Object overrideLayers(
            Object original, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) EquipmentSlot slot
	) {
		var overrides = CustomGlobalArmorOverrides.overrideArmor(itemStack, slot);
		return overrides.orElse((Equippable) original);
	}
}
