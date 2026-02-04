package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity
import moe.nea.notfimament.features.texturepack.CustomModelOverrideParser
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser
import net.minecraft.world.item.ItemStack

class AndPredicate(val children: Array<FirmamentModelPredicate>) : FirmamentModelPredicate {
	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		return children.all { it.test(stack, holder) }
	}

    object Parser : FirmamentModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmamentModelPredicate {
            val children =
                (jsonElement as JsonArray)
                    .flatMap {
	                    CustomModelOverrideParser.parsePredicates(it as JsonObject)
                    }
                    .toTypedArray()
            return AndPredicate(children)
        }

    }
}
