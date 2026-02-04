
package moe.nea.notfimament.events

import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ClickType

data class SlotClickEvent(
    val slot: Slot,
    val stack: ItemStack,
    val button: Int,
    val actionType: ClickType,
) : FirmamentEvent() {
    companion object : FirmamentEventBus<SlotClickEvent>()
}
