package moe.nea.notfimament.features.items.recipes

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import moe.nea.notfimament.util.MoulConfigUtils.createAndTranslateFullContext

class MoulConfigWidget(
	val component: GuiComponent,
	override var position: Point,
	override val size: Dimension,
) : RecipeWidget() {
	override fun render(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		createAndTranslateFullContext(
			guiGraphics, mouseX, mouseY, rect,
			component::render
		)
	}

	override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
		return createAndTranslateFullContext(null, event.x.toInt(), event.y.toInt(), rect) {
			component.mouseEvent(MouseEvent.Click(event.button(), true), it)
		}
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		createAndTranslateFullContext(null, mouseX, mouseY, rect) {
			component.mouseEvent(MouseEvent.Move(0F, 0F), it)
		}
	}

	override fun mouseReleased(event: MouseButtonEvent): Boolean {
		return createAndTranslateFullContext(null, event.x, event.y, rect) {
			component.mouseEvent(MouseEvent.Click(event.button(), false), it)
		}
	}

}
