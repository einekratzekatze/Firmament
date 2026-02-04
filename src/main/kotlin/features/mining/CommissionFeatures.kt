package moe.nea.notfimament.features.mining

import moe.nea.notfimament.Firmament
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.SlotRenderEvents
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.mc.loreAccordingToNbt
import moe.nea.notfimament.util.unformattedString

object CommissionFeatures {
	@Config
	object TConfig : ManagedConfig("commissions", Category.MINING) {
		val highlightCompletedCommissions by toggle("highlight-completed") { true }
	}


	@Subscribe
	fun onSlotRender(event: SlotRenderEvents.Before) {
		if (!TConfig.highlightCompletedCommissions) return
		if (MC.screenName != "Commissions") return
		val stack = event.slot.item
		if (stack.loreAccordingToNbt.any { it.unformattedString == "COMPLETED" }) {
			event.highlight(Firmament.identifier("completed_commission_background"))
		}
	}
}
