package moe.nea.notfimament.compat.rei

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import me.shedaniel.math.Dimension
import me.shedaniel.math.FloatingDimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import moe.nea.notfimament.compat.rei.recipes.wrapWidget
import moe.nea.notfimament.repo.SBItemStack
import moe.nea.notfimament.repo.recipes.RecipeLayouter

class REIRecipeLayouter : RecipeLayouter {
	val container: MutableList<Widget> = mutableListOf()
	fun <T: Widget> add(t: T): T = t.also(container::add)

	override fun createCyclingItemSlot(
		x: Int,
		y: Int,
		content: List<SBItemStack>,
		slotKind: RecipeLayouter.SlotKind
	): RecipeLayouter.CyclingItemSlot {
		val slot = Widgets.createSlot(Point(x, y))
		if (content.isNotEmpty())
			slot.entries(content.map { SBItemEntryDefinition.getEntry(it) })
		when (slotKind) {
			RecipeLayouter.SlotKind.SMALL_INPUT -> slot.markInput()
			RecipeLayouter.SlotKind.SMALL_OUTPUT -> slot.markOutput()
			RecipeLayouter.SlotKind.BIG_OUTPUT -> {
				slot.markOutput().disableBackground()
				add(Widgets.createResultSlotBackground(Point(x, y)))
			}
			RecipeLayouter.SlotKind.DISPLAY -> {
				slot.disableBackground()
				slot.disableHighlight()
			}
		}
		add(slot)
		return object : RecipeLayouter.CyclingItemSlot {
			override fun current(): SBItemStack = content.firstOrNull() ?: SBItemStack.EMPTY
			override fun update(newValue: SBItemStack) {}
			override fun onUpdate(action: () -> Unit) {}
		}
	}

	override fun createTooltip(rectangle: Rectangle, label: List<Component>) {
		add(Widgets.createTooltip(rectangle, *label.toTypedArray()))
	}

	override fun createLabel(x: Int, y: Int, text: Component): RecipeLayouter.Updater<Component> {
		val label = add(Widgets.createLabel(Point(x, y), text))
		return object : RecipeLayouter.Updater<Component> {
			override fun update(newValue: Component) {
				label.message = newValue
			}
		}
	}

	override fun createArrow(x: Int, y: Int) =
		add(Widgets.createArrow(Point(x, y))).bounds

	override fun createMoulConfig(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		component: GuiComponent
	) {
		add(wrapWidget(Rectangle(Point(x, y), Dimension(w, h)), component))
	}

	override fun createFire(point: Point, animationTicks: Int) {
		add(Widgets.createBurningFire(point).animationDurationTicks(animationTicks.toDouble()))
	}

	override fun createEntity(rectangle: Rectangle, entity: LivingEntity) {
		add(EntityWidget(entity, rectangle.location, FloatingDimension(rectangle.size)))
	}
}
