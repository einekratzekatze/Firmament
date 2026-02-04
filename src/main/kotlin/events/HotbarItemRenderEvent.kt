

package moe.nea.notfimament.events

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.DeltaTracker
import net.minecraft.world.item.ItemStack

data class HotbarItemRenderEvent(
    val item: ItemStack,
    val context: GuiGraphics,
    val x: Int,
    val y: Int,
    val tickDelta: DeltaTracker,
) : FirmamentEvent() {
    companion object : FirmamentEventBus<HotbarItemRenderEvent>()
}
