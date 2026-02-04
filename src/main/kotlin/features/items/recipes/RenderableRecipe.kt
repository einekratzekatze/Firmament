package moe.nea.notfimament.features.items.recipes

import java.util.Objects
import me.shedaniel.math.Rectangle
import moe.nea.notfimament.repo.SBItemStack
import moe.nea.notfimament.repo.recipes.GenericRecipeRenderer

class RenderableRecipe<T : Any>(
	val recipe: T,
	val renderer: GenericRecipeRenderer<T>,
	val mainItemStack: SBItemStack?,
) {
	fun render(bounds: Rectangle): StandaloneRecipeRenderer {
		val layouter = StandaloneRecipeRenderer(bounds)
		renderer.render(recipe, bounds, layouter, mainItemStack)
		return layouter
	}

//	override fun equals(other: Any?): Boolean {
//		if (other !is RenderableRecipe<*>) return false
//		return renderer == other.renderer && recipe == other.recipe
//	}
//
//	override fun hashCode(): Int {
//		return Objects.hash(recipe, renderer)
//	}
}
