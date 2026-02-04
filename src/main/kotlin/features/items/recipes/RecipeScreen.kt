package moe.nea.notfimament.features.items.recipes

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import moe.nea.notfimament.util.mc.CommonTextures
import moe.nea.notfimament.util.render.enableScissorWithTranslation
import moe.nea.notfimament.util.tr

class RecipeScreen(
	val recipes: List<RenderableRecipe<*>>,
) : Screen(tr("firmament.recipe.screen", "SkyBlock Recipe")) {

	data class PlacedRecipe(
		val bounds: Rectangle,
		val layoutedRecipe: StandaloneRecipeRenderer,
	) {
		fun moveTo(position: Point) {
			val Δx = position.x - bounds.x
			val Δy = position.y - bounds.y
			bounds.translate(Δx, Δy)
			layoutedRecipe.widgets.forEach { widget ->
				widget.position = widget.position.clone().also {
					it.translate(Δx, Δy)
				}
			}
		}
	}

	lateinit var placedRecipes: List<PlacedRecipe>
	var scrollViewport: Int = 0
	var scrollOffset: Int = 0
	var scrollPortWidth: Int = 0
	var heightEstimate: Int = 0
	val gutter = 10
	override fun init() {
		super.init()
		scrollViewport = minOf(height - 20, 250)
		scrollPortWidth = 0
		heightEstimate = 0
		var offset = height / 2 - scrollViewport / 2
		placedRecipes = recipes.map {
			val effectiveWidth = minOf(it.renderer.displayWidth, width - 20)
			val bounds = Rectangle(
				width / 2 - effectiveWidth / 2,
				offset,
				effectiveWidth,
				it.renderer.displayHeight
			)
			if (heightEstimate > 0)
				heightEstimate += gutter
			heightEstimate += bounds.height
			scrollPortWidth = maxOf(effectiveWidth, scrollPortWidth)
			offset += bounds.height + gutter
			val layoutedRecipe = it.render(bounds)
			layoutedRecipe.widgets.forEach(this::addRenderableWidget)
			PlacedRecipe(bounds, layoutedRecipe)
		}
	}

	fun scrollRect() =
		Rectangle(
			width / 2 - scrollPortWidth / 2, height / 2 - scrollViewport / 2,
			scrollPortWidth, scrollViewport
		)

	fun scissorScrollPort(guiGraphics: GuiGraphics) {
		guiGraphics.enableScissorWithTranslation(scrollRect())
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!scrollRect().contains(mouseX, mouseY))
			return false
		scrollOffset = (scrollOffset + scrollY * -4)
			.coerceAtMost(heightEstimate - scrollViewport.toDouble())
			.coerceAtLeast(.0)
			.toInt()
		var offset = height / 2 - scrollViewport / 2 - scrollOffset
		placedRecipes.forEach {
			it.moveTo(Point(it.bounds.x, offset))
			offset += it.bounds.height + gutter
		}
		return true
	}

	override fun renderBackground(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)

		val srect = scrollRect()
		srect.grow(8, 8)
		guiGraphics.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			CommonTextures.genericWidget(),
			srect.x, srect.y,
			srect.width, srect.height
		)

		scissorScrollPort(guiGraphics)
		placedRecipes.forEach {
			guiGraphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				CommonTextures.genericWidget(),
				it.bounds.x, it.bounds.y,
				it.bounds.width, it.bounds.height
			)
		}
		guiGraphics.disableScissor()
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		scissorScrollPort(guiGraphics)
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.disableScissor()
	}

	override fun tick() {
		super.tick()
		placedRecipes.forEach {
			it.layoutedRecipe.tick()
		}
	}
}
