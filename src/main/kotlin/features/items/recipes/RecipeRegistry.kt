package moe.nea.notfimament.features.items.recipes

import com.mojang.blaze3d.platform.InputConstants
import io.github.moulberry.repo.IReloadable
import io.github.moulberry.repo.NEURepository
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.HandledScreenKeyPressedEvent
import moe.nea.notfimament.events.ReloadRegistrationEvent
import moe.nea.notfimament.keybindings.SavedKeyBinding
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.repo.SBItemStack
import moe.nea.notfimament.repo.recipes.GenericRecipeRenderer
import moe.nea.notfimament.repo.recipes.SBCraftingRecipeRenderer
import moe.nea.notfimament.repo.recipes.SBEssenceUpgradeRecipeRenderer
import moe.nea.notfimament.repo.recipes.SBForgeRecipeRenderer
import moe.nea.notfimament.repo.recipes.SBReforgeRecipeRenderer
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.SkyblockId
import moe.nea.notfimament.util.focusedItemStack

object RecipeRegistry {
	val recipeTypes: List<GenericRecipeRenderer<*>> = listOf(
		SBCraftingRecipeRenderer,
		SBForgeRecipeRenderer,
		SBReforgeRecipeRenderer,
		SBEssenceUpgradeRecipeRenderer,
	)


	@Subscribe
	fun showUsages(event: HandledScreenKeyPressedEvent) {
		if (!ItemList.isItemListEnabled) return
		val provider =
			if (event.matches(SavedKeyBinding.keyWithoutMods(InputConstants.KEY_R))) {
				::getRecipesFor
			} else if (event.matches(SavedKeyBinding.keyWithoutMods(InputConstants.KEY_U))) {
				::getUsagesFor
			} else {
				return
			}
		val stack = event.screen.focusedItemStack ?: return
		val recipes = provider(SBItemStack(stack))
		if (recipes.isEmpty()) return
		MC.screen = RecipeScreen(recipes.toList())
	}


	object RecipeIndexes : IReloadable {

		private fun <T : Any> createIndexFor(
			neuRepository: NEURepository,
			recipeRenderer: GenericRecipeRenderer<T>,
			outputs: Boolean,
		): List<Pair<SkyblockId, RenderableRecipe<T>>> {
			val indexer: (T) -> Collection<SBItemStack> =
				if (outputs) recipeRenderer::getOutputs
				else recipeRenderer::getInputs
			return recipeRenderer.findAllRecipes(neuRepository)
				.flatMap {
					val wrappedRecipe = RenderableRecipe(it, recipeRenderer, null)
					indexer(it).map { it.skyblockId to wrappedRecipe }
				}
		}

		fun createIndex(outputs: Boolean): MutableMap<SkyblockId, List<RenderableRecipe<*>>> {
			val m: MutableMap<SkyblockId, List<RenderableRecipe<*>>> = mutableMapOf()
			recipeTypes.forEach { renderer ->
				createIndexFor(RepoManager.neuRepo, renderer, outputs)
					.forEach { (stack, recipe) ->
						m.merge(stack, listOf(recipe)) { a, b -> a + b }
					}
			}
			return m
		}

		lateinit var recipesForIndex: Map<SkyblockId, List<RenderableRecipe<*>>>
		lateinit var usagesForIndex: Map<SkyblockId, List<RenderableRecipe<*>>>
		override fun reload(recipe: NEURepository) {
			recipesForIndex = createIndex(true)
			usagesForIndex = createIndex(false)
		}
	}

	@Subscribe
	fun onRepoBuild(event: ReloadRegistrationEvent) {
		event.repo.registerReloadListener(RecipeIndexes)
	}


	fun getRecipesFor(itemStack: SBItemStack): Set<RenderableRecipe<*>> {
		val recipes = LinkedHashSet<RenderableRecipe<*>>()
		recipeTypes.forEach { injectRecipesFor(it, recipes, itemStack, true) }
		recipes.addAll(RecipeIndexes.recipesForIndex[itemStack.skyblockId] ?: emptyList())
		return recipes
	}

	fun getUsagesFor(itemStack: SBItemStack): Set<RenderableRecipe<*>> {
		val recipes = LinkedHashSet<RenderableRecipe<*>>()
		recipeTypes.forEach { injectRecipesFor(it, recipes, itemStack, false) }
		recipes.addAll(RecipeIndexes.usagesForIndex[itemStack.skyblockId] ?: emptyList())
		return recipes
	}

	private fun <T : Any> injectRecipesFor(
		recipeRenderer: GenericRecipeRenderer<T>,
		collector: MutableCollection<RenderableRecipe<*>>,
		relevantItem: SBItemStack,
		mustBeInOutputs: Boolean
	) {
		collector.addAll(
			recipeRenderer.discoverExtraRecipes(RepoManager.neuRepo, relevantItem, mustBeInOutputs)
				.map { RenderableRecipe(it, recipeRenderer, relevantItem) }
		)
	}


}
