package moe.nea.notfimament.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import moe.nea.notfimament.repo.recipes.RecipeLayouter
import moe.nea.notfimament.util.MC

class ComponentWidget(override var position: Point, var text: Component) : RecipeWidget(), RecipeLayouter.Updater<Component> {
	override fun update(newValue: Component) {
		this.text = newValue
	}

	override val size: Dimension
		get() = Dimension(MC.font.width(text), MC.font.lineHeight)

	override fun render(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		guiGraphics.drawString(MC.font, text, position.x, position.y, -1)
	}
}
