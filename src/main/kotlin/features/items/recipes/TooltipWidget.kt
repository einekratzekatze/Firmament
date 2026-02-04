package moe.nea.notfimament.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import moe.nea.notfimament.repo.recipes.RecipeLayouter

class TooltipWidget(
	override var position: Point,
	override val size: Dimension,
	label: List<Component>
) : RecipeWidget(), RecipeLayouter.Updater<List<Component>> {
	override fun update(newValue: List<Component>) {
		this.formattedComponent = newValue.map { it.visualOrderText }
	}

	var formattedComponent = label.map { it.visualOrderText }
	override fun render(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		if (rect.contains(mouseX, mouseY))
			guiGraphics.setTooltipForNextFrame(formattedComponent, mouseX, mouseY)
	}

}
