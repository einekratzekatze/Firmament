

package moe.nea.notfimament.events

import net.minecraft.network.chat.Component
import moe.nea.notfimament.util.unformattedString

/**
 * Allow modification of a chat message before it is sent off to the user. Intended for display purposes.
 */
data class ModifyChatEvent(val originalText: Component) : FirmamentEvent() {
    var unformattedString = originalText.unformattedString
        private set
    var replaceWith: Component = originalText
        set(value) {
            field = value
            unformattedString = value.unformattedString
        }

    companion object : FirmamentEventBus<ModifyChatEvent>()
}
