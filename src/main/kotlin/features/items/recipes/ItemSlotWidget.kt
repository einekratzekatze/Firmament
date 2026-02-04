package moe.nea.notfimament.features.items.recipes

import java.util.Optional
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import moe.nea.notfimament.api.v1.FirmamentItemWidget
import moe.nea.notfimament.events.ItemTooltipEvent
import moe.nea.notfimament.keybindings.SavedKeyBinding
import moe.nea.notfimament.repo.ExpensiveItemCacheApi
import moe.nea.notfimament.repo.SBItemStack
import moe.nea.notfimament.repo.recipes.RecipeLayouter
import moe.nea.notfimament.util.ErrorUtil
import moe.nea.notfimament.util.FirmFormatters.shortFormat
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.darkGrey
import moe.nea.notfimament.util.mc.displayNameAccordingToNbt
import moe.nea.notfimament.util.mc.loreAccordingToNbt

class ItemSlotWidget(
	point: Point,
	var content: List<SBItemStack>,
	val slotKind: RecipeLayouter.SlotKind
) : RecipeWidget(),
	RecipeLayouter.CyclingItemSlot,
	FirmamentItemWidget {
	override var position = point
	override val size get() = Dimension(16, 16)
	val itemRect get() = Rectangle(position, Dimension(16, 16))

	val backgroundTopLeft
		get() =
			if (slotKind.isBig) Point(position.x - 4, position.y - 4)
			else Point(position.x - 1, position.y - 1)
	val backgroundSize =
		if (slotKind.isBig) Dimension(16 + 8, 16 + 8)
		else Dimension(18, 18)
	override val rect: Rectangle
		get() = Rectangle(backgroundTopLeft, backgroundSize)

	@OptIn(ExpensiveItemCacheApi::class)
	override fun render(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
		val stack = current().asImmutableItemStack()
		// TODO: draw slot background
		if (stack.isEmpty) return
		guiGraphics.renderItem(stack, position.x, position.y)
		guiGraphics.renderItemDecorations(
			MC.font, stack, position.x, position.y,
			if (stack.count >= SHORT_NUM_CUTOFF) shortFormat(stack.count.toDouble())
			else null
		)
		if (itemRect.contains(mouseX, mouseY)
			&& guiGraphics.containsPointInScissor(mouseX, mouseY)
		) guiGraphics.setTooltipForNextFrame(
			MC.font, getTooltip(stack), Optional.empty(),
			mouseX, mouseY
		)
	}

	companion object {
		val SHORT_NUM_CUTOFF = 1000
		var canUseTooltipEvent = true

		fun getTooltip(itemStack: ItemStack): List<Component> {
			val lore = mutableListOf(itemStack.displayNameAccordingToNbt)
			lore.addAll(itemStack.loreAccordingToNbt)
			if (canUseTooltipEvent) {
				try {
					ItemTooltipCallback.EVENT.invoker().getTooltip(
						itemStack, Item.TooltipContext.EMPTY,
						TooltipFlag.NORMAL, lore
					)
				} catch (ex: Exception) {
					canUseTooltipEvent = false
					ErrorUtil.softError("Failed to use vanilla tooltips", ex)
				}
			} else {
				ItemTooltipEvent.publish(
					ItemTooltipEvent(
						itemStack,
						Item.TooltipContext.EMPTY,
						TooltipFlag.NORMAL,
						lore
					)
				)
			}
			if (itemStack.count >= SHORT_NUM_CUTOFF && lore.isNotEmpty())
				lore.add(1, Component.literal("${itemStack.count}x").darkGrey())
			return lore
		}
	}


	override fun tick() {
		if (SavedKeyBinding.isShiftDown()) return
		if (content.size <= 1) return
		if (MC.currentTick % 5 != 0) return
		index = (index + 1) % content.size
	}

	var index = 0
	var onUpdate: () -> Unit = {}

	override fun onUpdate(action: () -> Unit) {
		this.onUpdate = action
	}

	override fun current(): SBItemStack {
		return content.getOrElse(index) { SBItemStack.EMPTY }
	}

	override fun update(newValue: SBItemStack) {
		content = listOf(newValue)
		// SAFE: content was just assigned to a non-empty list
		index = index.coerceIn(content.indices)
	}

	override fun getPlacement(): FirmamentItemWidget.Placement {
		return FirmamentItemWidget.Placement.RECIPE_SCREEN
	}

	@OptIn(ExpensiveItemCacheApi::class)
	override fun getItemStack(): ItemStack {
		return current().asImmutableItemStack()
	}

	override fun getSkyBlockId(): String {
		return current().skyblockId.neuItem
	}
}
