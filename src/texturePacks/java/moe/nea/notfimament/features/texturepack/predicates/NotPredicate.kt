
package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.notfimament.features.texturepack.CustomModelOverrideParser
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser
import net.minecraft.world.item.ItemStack

class NotPredicate(val children: Array<FirmamentModelPredicate>) : FirmamentModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return children.none { it.test(stack) }
    }

    object Parser : FirmamentModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmamentModelPredicate {
            return NotPredicate(CustomModelOverrideParser.parsePredicates(jsonElement as JsonObject).toTypedArray())
        }
    }
}
