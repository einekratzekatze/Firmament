

package moe.nea.notfimament.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import moe.nea.notfimament.Firmament

object ScreenUtil {
    init {
        ClientTickEvents.START_CLIENT_TICK.register(::onTick)
    }

    private fun onTick(minecraft: Minecraft) {
        if (nextOpenedGui != null) {
            val p = minecraft.player
            if (p?.containerMenu != null) {
                p.closeContainer()
            }
            minecraft.setScreen(nextOpenedGui)
            nextOpenedGui = null
        }
    }

    private var nextOpenedGui: Screen? = null

    fun setScreenLater(nextScreen: Screen?) {
        val nog = nextOpenedGui
        if (nog != null) {
            Firmament.logger.warn("Setting screen ${if (nextScreen == null) "null" else nextScreen::class.qualifiedName} to be opened later, but ${nog::class.qualifiedName} is already queued.")
            return
        }
        nextOpenedGui = nextScreen
    }


}
