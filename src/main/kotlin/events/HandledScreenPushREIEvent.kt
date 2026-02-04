

package moe.nea.notfimament.events

import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenPushREIEvent(
    val screen: AbstractContainerScreen<*>,
    val rectangles: MutableList<Rectangle> = mutableListOf()
) : FirmamentEvent() {

    fun block(rectangle: Rectangle) {
        rectangles.add(rectangle)
    }

    companion object : FirmamentEventBus<HandledScreenPushREIEvent>()
}
