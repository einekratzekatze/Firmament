package moe.nea.notfimament.features.items.recipes

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import moe.nea.notfimament.repo.SBItemStack
import moe.nea.notfimament.repo.recipes.RecipeLayouter

class StandaloneRecipeRenderer(val bounds: Rectangle) : AbstractContainerEventHandler(), RecipeLayouter {

	fun tick() {
		widgets.forEach { it.tick() }
	}

	fun <T : RecipeWidget> addWidget(widget: T): T {
		this.widgets.add(widget)
		return widget
	}

	override fun createCyclingItemSlot(
		x: Int,
		y: Int,
		content: List<SBItemStack>,
		slotKind: RecipeLayouter.SlotKind
	): RecipeLayouter.CyclingItemSlot {
		return addWidget(ItemSlotWidget(Point(x, y), content, slotKind))
	}

	val Rectangle.topLeft get() = Point(x, y)

	override fun createTooltip(
		rectangle: Rectangle,
		label: List<Component>
	) {
		addWidget(TooltipWidget(rectangle.topLeft, rectangle.size, label))
	}

	override fun createLabel(
		x: Int,
		y: Int,
		text: Component
	): RecipeLayouter.Updater<Component> {
		return addWidget(ComponentWidget(Point(x, y), text))
	}

	override fun createArrow(x: Int, y: Int): Rectangle {
		return addWidget(ArrowWidget(Point(x, y))).rect
	}

	override fun createMoulConfig(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		component: GuiComponent
	) {
		addWidget(MoulConfigWidget(component, Point(x, y), Dimension(w, h)))
	}

	override fun createFire(point: Point, animationTicks: Int) {
		addWidget(FireWidget(point, animationTicks))
	}

	override fun createEntity(rectangle: Rectangle, entity: LivingEntity) {
		addWidget(EntityWidget(rectangle.topLeft, rectangle.size, entity))
	}

	val widgets: MutableList<RecipeWidget> = mutableListOf()
	override fun children(): List<GuiEventListener> {
		return widgets
	}
}
