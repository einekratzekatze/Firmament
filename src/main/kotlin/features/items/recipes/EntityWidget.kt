package moe.nea.notfimament.features.items.recipes

import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.LivingEntity
import moe.nea.notfimament.gui.entity.EntityRenderer

class EntityWidget(
	override var position: Point,
	override val size: Dimension,
	val entity: LivingEntity
) : RecipeWidget() {
	override fun render(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		EntityRenderer.renderEntity(
			entity, guiGraphics,
			rect.x, rect.y,
			rect.width.toDouble(), rect.height.toDouble(),
			mouseX.toDouble(), mouseY.toDouble()
		)
	}
}
