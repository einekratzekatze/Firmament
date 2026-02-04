package moe.nea.notfimament.events

import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.util.MC

sealed class ChestInventoryUpdateEvent : FirmamentEvent() {
	companion object : FirmamentEventBus<ChestInventoryUpdateEvent>()
	data class Single(val slot: Int, val stack: ItemStack) : ChestInventoryUpdateEvent()
	data class Multi(val contents: List<ItemStack>) : ChestInventoryUpdateEvent()
	val inventory = MC.screen
}
