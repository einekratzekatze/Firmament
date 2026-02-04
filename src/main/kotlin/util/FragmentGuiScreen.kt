

package moe.nea.notfimament.util

import io.github.notenoughupdates.moulconfig.gui.GuiContext
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component

abstract class FragmentGuiScreen(
    val dismissOnOutOfBounds: Boolean = true
) : Screen(Component.literal("")) {
    var popup: MoulConfigFragment? = null

    fun createPopup(context: GuiContext, position: Point) {
        popup = MoulConfigFragment(context, position) { popup = null }
    }

	fun renderPopup(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
		popup?.render(context, mouseX, mouseY, delta)
	}

    private inline fun ifPopup(ifYes: (MoulConfigFragment) -> Unit): Boolean {
        val p = popup ?: return false
        ifYes(p)
        return true
    }

	override fun keyPressed(input: KeyEvent): Boolean {
        return ifPopup {
            it.keyPressed(input)
        }
    }

	override fun keyReleased(input: KeyEvent): Boolean {
        return ifPopup {
            it.keyReleased(input)
        }
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        ifPopup { it.mouseMoved(mouseX, mouseY) }
    }

	override fun mouseReleased(click: MouseButtonEvent): Boolean {
        return ifPopup {
            it.mouseReleased(click)
        }
    }

	override fun mouseDragged(click: MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean {
        return ifPopup {
            it.mouseDragged(click, offsetX, offsetY)
        }
    }

	override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        return ifPopup {
            if (!Rectangle(
                    it.position,
                    Dimension(it.guiContext.root.width, it.guiContext.root.height)
                ).contains(Point(click.x, click.y))
                && dismissOnOutOfBounds
            ) {
                popup = null
            } else {
                it.mouseClicked(click, doubled)
            }
        }|| super.mouseClicked(click, doubled)
    }

	override fun charTyped(input: CharacterEvent): Boolean {
        return ifPopup { it.charTyped(input) }
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        return ifPopup {
            it.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        }
    }
}
