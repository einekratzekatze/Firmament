package moe.nea.notfimament.features.macros

import kotlinx.serialization.Serializable
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.DataHolder

@Serializable
data class MacroData(
	var comboActions: List<ComboKeyAction> = listOf(),
	var wheels: List<MacroWheel> = listOf(),
) {
	@Config
	object DConfig : DataHolder<MacroData>(kotlinx.serialization.serializer(), "macros", ::MacroData) {
		override fun onLoad() {
			ComboProcessor.setActions(data.comboActions)
			RadialMacros.setWheels(data.wheels)
		}
	}
}
