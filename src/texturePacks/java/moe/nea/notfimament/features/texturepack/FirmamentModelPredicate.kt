package moe.nea.notfimament.features.texturepack

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

@Serializable(with = FirmamentRootPredicateSerializer::class)
interface FirmamentModelPredicate {
	fun test(stack: ItemStack, holder: LivingEntity?): Boolean = test(stack)
	fun test(stack: ItemStack): Boolean = test(stack, null)
}
