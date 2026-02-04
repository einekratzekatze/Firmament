
package moe.nea.notfimament.util.customgui

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

@Suppress("FunctionName")
interface HasCustomGui {
    fun getCustomGui_Firmament(): CustomGui?
    fun setCustomGui_Firmament(gui: CustomGui?)
}

var <T : AbstractContainerScreen<*>> T.customGui: CustomGui?
    get() = (this as HasCustomGui).getCustomGui_Firmament()
    set(value) {
        (this as HasCustomGui).setCustomGui_Firmament(value)
    }

