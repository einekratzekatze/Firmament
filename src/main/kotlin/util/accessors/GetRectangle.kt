

package moe.nea.notfimament.util.accessors

import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import moe.nea.notfimament.mixins.accessor.AccessorHandledScreen

fun AbstractContainerScreen<*>.getProperRectangle(): Rectangle {
    this.castAccessor()
    return Rectangle(
        getX_Firmament(),
        getY_Firmament(),
        getBackgroundWidth_Firmament(),
        getBackgroundHeight_Firmament()
    )
}
