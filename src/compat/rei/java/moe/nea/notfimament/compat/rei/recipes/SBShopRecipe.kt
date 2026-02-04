package moe.nea.notfimament.compat.rei.recipes

import io.github.moulberry.repo.data.NEUNpcShopRecipe
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import net.minecraft.world.item.Items
import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.compat.rei.SBItemEntryDefinition
import moe.nea.notfimament.util.skyblockId

class SBShopRecipe(override val neuRecipe: NEUNpcShopRecipe) : SBRecipe() {
	override fun getCategoryIdentifier(): CategoryIdentifier<*> = Category.catIdentifier
	val merchant = SBItemEntryDefinition.getEntry(neuRecipe.isSoldBy.skyblockId)
	override fun getInputEntries(): List<EntryIngredient> {
		return listOf(EntryIngredient.of(merchant)) + super.getInputEntries()
	}

	object Category : DisplayCategory<SBShopRecipe> {
		val catIdentifier = CategoryIdentifier.of<SBShopRecipe>(Firmament.MOD_ID, "npc_shopping")
		override fun getCategoryIdentifier(): CategoryIdentifier<SBShopRecipe> = catIdentifier

		override fun getTitle(): Component = Component.literal("SkyBlock NPC Shopping")

		override fun getIcon(): Renderer = SBItemEntryDefinition.getPassthrough(Items.EMERALD)
		override fun setupDisplay(display: SBShopRecipe, bounds: Rectangle): List<Widget> {
			val point = Point(bounds.centerX, bounds.centerY)
			return buildList {
				add(Widgets.createRecipeBase(bounds))
				add(Widgets.createSlot(Point(point.x - 2 - 18 / 2, point.y - 18 - 6))
					    .unmarkInputOrOutput()
					    .entry(display.merchant)
					    .disableBackground())
				add(Widgets.createArrow(Point(point.x - 2 - 24 / 2, point.y - 6)))
				val cost = display.neuRecipe.cost
				for ((i, item) in cost.withIndex()) {
					add(Widgets.createSlot(Point(
						point.x - 14 - 18,
						point.y + i * 18 - 18 * cost.size / 2))
						    .entry(SBItemEntryDefinition.getEntry(item))
						    .markInput())
					// TODO: fix frame clipping
				}
				add(Widgets.createResultSlotBackground(Point(point.x + 18, point.y - 18 / 2)))
				add(
					Widgets.createSlot(Point(point.x + 18, point.y - 18 / 2))
						.entry(SBItemEntryDefinition.getEntry(display.neuRecipe.result))
						.disableBackground().markOutput()
				)
			}
		}

	}

}
