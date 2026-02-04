
package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser
import moe.nea.notfimament.features.texturepack.StringMatcher
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.util.mc.displayNameAccordingToNbt

data class DisplayNamePredicate(val stringMatcher: StringMatcher) : FirmamentModelPredicate {
    override fun test(stack: ItemStack): Boolean {
        val display = stack.displayNameAccordingToNbt
        return stringMatcher.matches(display)
    }

    object Parser : FirmamentModelPredicateParser {
        override fun parse(jsonElement: JsonElement): FirmamentModelPredicate {
            return DisplayNamePredicate(StringMatcher.parse(jsonElement))
        }
    }
}
