

package moe.nea.notfimament.events

import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.network.chat.Component

data class ItemTooltipEvent(
    val stack: ItemStack, val context: TooltipContext, val type: TooltipFlag, val lines: MutableList<Component>
) : FirmamentEvent() {
    companion object : FirmamentEventBus<ItemTooltipEvent>()
}
