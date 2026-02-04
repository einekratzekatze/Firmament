
package moe.nea.notfimament.util

import me.shedaniel.math.Color
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.events.FirmamentEvent
import moe.nea.notfimament.events.FirmamentEventBus

data class DurabilityBarEvent(
    val item: ItemStack,
) : FirmamentEvent() {
    data class DurabilityBar(
        val color: Color,
        val percentage: Float,
    )

    var barOverride: DurabilityBar? = null

    companion object : FirmamentEventBus<DurabilityBarEvent>()
}
