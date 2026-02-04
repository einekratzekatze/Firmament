package moe.nea.notfimament.features.inventory

import org.lwjgl.glfw.GLFW
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.SlotRenderEvents
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.skyblock.SBItemUtil.getSearchName
import moe.nea.notfimament.util.useMatch

object JunkHighlighter {
	val identifier: String
		get() = "junk-highlighter"

	@Config
	object TConfig : ManagedConfig(identifier, Category.INVENTORY) {
		val junkRegex by string("regex") { "" }
		val highlightBind by keyBinding("highlight") { GLFW.GLFW_KEY_LEFT_CONTROL }
	}

	@Subscribe
	fun onDrawSlot(event: SlotRenderEvents.After) {
		if (!TConfig.highlightBind.isPressed() || TConfig.junkRegex.isEmpty()) return
		val junkRegex = TConfig.junkRegex.toPattern()
		val slot = event.slot
		junkRegex.useMatch(slot.item.getSearchName()) {
			event.context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xffff0000.toInt())
		}
	}
}
