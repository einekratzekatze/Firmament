package moe.nea.notfimament.events

import org.lwjgl.glfw.GLFW
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import moe.nea.notfimament.events.HandledScreenKeyPressedEvent.Companion.internalPollAction
import moe.nea.notfimament.keybindings.GenericInputAction
import moe.nea.notfimament.keybindings.InputModifiers
import moe.nea.notfimament.keybindings.SavedKeyBinding

sealed interface HandledScreenInputEvent {
	val screen: Screen
	val input: GenericInputAction
	val modifiers: InputModifiers
}

data class HandledScreenKeyPressedEvent(
	override val screen: Screen,
	override val input: GenericInputAction,
	override val modifiers: InputModifiers,
) : FirmamentEvent.Cancellable(), HandledScreenInputEvent {
	val isRepeat: Boolean = internalPollAction() == GLFW.GLFW_REPEAT
	fun matches(keyBinding: SavedKeyBinding, atLeast: Boolean = false): Boolean {
		return keyBinding.matches(input, modifiers, atLeast)
	}

	fun isLeftClick() = input == GenericInputAction.mouse(GLFW.GLFW_MOUSE_BUTTON_LEFT)

	companion object : FirmamentEventBus<HandledScreenKeyPressedEvent>() {
		private var lastAction = -1

		/**
		 * save the exact action last passed to [net.minecraft.client.KeyboardHandler.keyPress]. can be restored using
		 * [internalPollAction]
		 */
		fun internalPushAction(action: Int) {
			lastAction = action
		}

		fun internalPollAction(): Int {
			return lastAction
		}
	}
}

data class HandledScreenKeyReleasedEvent(
	override val screen: AbstractContainerScreen<*>,
	override val input: GenericInputAction,
	override val modifiers: InputModifiers,
) : FirmamentEvent.Cancellable(), HandledScreenInputEvent {
	fun matches(keyBinding: SavedKeyBinding, atLeast: Boolean = false): Boolean {
		return keyBinding.matches(input, modifiers, atLeast)
	}

	companion object : FirmamentEventBus<HandledScreenKeyReleasedEvent>()
}
