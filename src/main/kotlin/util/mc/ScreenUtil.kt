package moe.nea.notfimament.util.mc

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot

object ScreenUtil {
	private var lastScreen: Screen? = null
	private var slotsByIndex: Map<SlotIndex, Slot> = mapOf()

	data class SlotIndex(val index: Int, val isPlayerInventory: Boolean)

	fun Screen.getSlotsByIndex(): Map<SlotIndex, Slot> {
		if (this !is AbstractContainerScreen<*>) return mapOf()
		if (lastScreen === this) return slotsByIndex
		lastScreen = this
		slotsByIndex = this.menu.slots.associate {
			SlotIndex(it.containerSlot, it.container is Inventory) to it
		}
		return slotsByIndex
	}

	fun Screen.getSlotByIndex(index: Int, isPlayerInventory: Boolean): Slot? =
		getSlotsByIndex()[SlotIndex(index, isPlayerInventory)]
}
