
package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser
import net.minecraft.world.item.ItemStack

object AlwaysPredicate : FirmamentModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        return true
    }

    object Parser : FirmamentModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmamentModelPredicate {
            return AlwaysPredicate
        }
    }
}
