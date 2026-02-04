package moe.nea.notfimament.events

import moe.nea.notfimament.keybindings.GenericInputAction
import moe.nea.notfimament.keybindings.InputModifiers
import moe.nea.notfimament.keybindings.SavedKeyBinding

data class WorldKeyboardEvent(val action: GenericInputAction, val modifiers: InputModifiers) : FirmamentEvent.Cancellable() {
	fun matches(keyBinding: SavedKeyBinding, atLeast: Boolean = false): Boolean {
		return keyBinding.matches(action, modifiers, atLeast)
	}

	companion object : FirmamentEventBus<WorldKeyboardEvent>()
}
