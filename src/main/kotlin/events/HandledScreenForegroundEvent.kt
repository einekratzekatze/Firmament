

package moe.nea.notfimament.events

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenForegroundEvent(
    val screen: AbstractContainerScreen<*>,
    val context: GuiGraphics,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : FirmamentEvent() {
    companion object : FirmamentEventBus<HandledScreenForegroundEvent>()
}
