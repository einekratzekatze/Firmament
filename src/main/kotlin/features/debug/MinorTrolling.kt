

package moe.nea.notfimament.features.debug

import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ModifyChatEvent

// In memorian Dulkir
object MinorTrolling {
    val identifier: String
        get() = "minor-trolling"

    val trollers = listOf("nea89o", "lrg89")
    val t = "From(?: \\[[^\\]]+])? ([^:]+): (.*)".toRegex()

    @Subscribe
    fun onTroll(it: ModifyChatEvent) {
        val m = t.matchEntire(it.unformattedString) ?: return
        val (_, name, text) = m.groupValues
        if (name !in trollers) return
        if (!text.startsWith("c:")) return
        it.replaceWith = Component.literal(text.substring(2).replace("&", "§"))
    }
}
