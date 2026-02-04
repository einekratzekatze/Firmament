package moe.nea.notfimament.events

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class HandledScreenClickEvent(
	val screen: AbstractContainerScreen<*>,
	val mouseX: Double, val mouseY: Double, val button: Int
) :
	FirmamentEvent.Cancellable() {
	companion object : FirmamentEventBus<HandledScreenClickEvent>()
}
