

package moe.nea.notfimament.compat.rei

import dev.architectury.event.CompoundEventResult
import me.shedaniel.math.Point
import me.shedaniel.rei.api.client.registry.screen.FocusedStackProvider
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import moe.nea.notfimament.mixins.accessor.AccessorHandledScreen

object SkyblockItemIdFocusedStackProvider : FocusedStackProvider {
    override fun provide(screen: Screen?, mouse: Point?): CompoundEventResult<EntryStack<*>> {
        if (screen !is AbstractContainerScreen<*>) return CompoundEventResult.pass()
        if (screen !is AccessorHandledScreen) return CompoundEventResult.pass()
        val focusedSlot = screen.focusedSlot_Firmament ?: return CompoundEventResult.pass()
        val item = focusedSlot.item ?: return CompoundEventResult.pass()
        return CompoundEventResult.interruptTrue(SBItemEntryDefinition.getEntry(item))
    }

    override fun getPriority(): Double = 1_000_000.0
}
