package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser

class CastPredicate : FirmamentModelPredicate {
	object Parser : FirmamentModelPredicateParser {
		override fun parse(jsonElement: JsonElement): FirmamentModelPredicate? {
			if (jsonElement.asDouble >= 1) return CastPredicate()
			return NotPredicate(arrayOf(CastPredicate()))
		}
	}

	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		return (holder as? Player)?.fishing != null && holder.mainHandItem === stack
	}

	override fun test(stack: ItemStack): Boolean {
		return false
	}
}
