package moe.nea.notfimament.gui.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import moe.nea.notfimament.gui.FirmButtonComponent
import moe.nea.notfimament.keybindings.FirmamentKeyBindings
import moe.nea.notfimament.keybindings.SavedKeyBinding
import moe.nea.notfimament.util.data.ManagedConfig

class KeyBindingHandler(val name: String, val managedConfig: ManagedConfig) :
	ManagedConfig.OptionHandler<SavedKeyBinding> {

	override fun initOption(opt: ManagedOption<SavedKeyBinding>) {
		FirmamentKeyBindings.registerKeyBinding(name, opt)
	}

	override fun toJson(element: SavedKeyBinding): JsonElement? {
		return Json.encodeToJsonElement(element)
	}

	override fun fromJson(element: JsonElement): SavedKeyBinding {
		return Json.decodeFromJsonElement(element)
	}

	fun createButtonComponent(opt: ManagedOption<SavedKeyBinding>): FirmButtonComponent {
		lateinit var button: FirmButtonComponent
		val sm = KeyBindingStateManager(
			{ opt.value },
			{
				opt.value = it
				opt.element.markDirty()
			},
			{ button.blur() },
			{ button.requestFocus() }
		)
		button = sm.createButton()
		sm.updateLabel()
		return button
	}

	override fun emitGuiElements(opt: ManagedOption<SavedKeyBinding>, guiAppender: GuiAppender) {
		guiAppender.appendLabeledRow(opt.labelText, createButtonComponent(opt))
	}

}
