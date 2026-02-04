package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser

class PullingPredicate(val percentage: Double) : FirmamentModelPredicate {
	companion object {
		val AnyPulling = PullingPredicate(0.1)
	}

	object Parser : FirmamentModelPredicateParser {
		override fun parse(jsonElement: JsonElement): FirmamentModelPredicate? {
			return PullingPredicate(jsonElement.asDouble)
		}
	}

	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		if (holder == null) return false
		return BowItem.getPowerForTime(holder.ticksUsingItem) >= percentage
	}

}
