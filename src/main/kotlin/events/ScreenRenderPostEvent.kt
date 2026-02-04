

package moe.nea.notfimament.events

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen

data class ScreenRenderPostEvent(
    val screen: Screen,
    val mouseX: Int,
    val mouseY: Int,
    val tickDelta: Float,
    val drawContext: GuiGraphics
) : FirmamentEvent() {
    companion object : FirmamentEventBus<ScreenRenderPostEvent>()
}
