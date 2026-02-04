package moe.nea.notfimament.repo

import io.github.moulberry.repo.data.NEURecipe

interface ExtraRecipeProvider {
	fun provideExtraRecipes(): Iterable<NEURecipe>
}
