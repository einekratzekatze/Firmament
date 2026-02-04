package moe.nea.notfimament.util.mc

import util.mc.FakeInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.Slot

class FakeSlot(
    stack: ItemStack,
    x: Int,
    y: Int
) : Slot(FakeInventory(stack), 0, x, y) {
	init {
		index = 0
	}
}
