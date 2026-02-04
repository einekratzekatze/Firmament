package moe.nea.notfimament.gui.config

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.deps.libninepatch.NinePatch
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent
import io.github.notenoughupdates.moulconfig.platform.MoulConfigPlatform
import org.lwjgl.glfw.GLFW
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import moe.nea.notfimament.gui.FirmButtonComponent
import moe.nea.notfimament.keybindings.GenericInputButton
import moe.nea.notfimament.keybindings.InputModifiers
import moe.nea.notfimament.keybindings.SavedKeyBinding

class KeyBindingStateManager(
	val value: () -> SavedKeyBinding,
	val setValue: (key: SavedKeyBinding) -> Unit,
	val blur: () -> Unit,
	val requestFocus: () -> Unit,
) {
	var editing = false
	var lastPressed: GenericInputButton? = null
	var label: Component = Component.literal("")

	fun onClick(mouseButton: Int) {
		if (editing) {
			keyboardEvent(GenericInputButton.mouse(mouseButton), true)
		} else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			editing = true
			requestFocus()
		}
		updateLabel()
	}

	fun keyboardEvent(keyCode: GenericInputButton, pressed: Boolean): Boolean {
		return if (pressed) onKeyPressed(keyCode, InputModifiers.current())
		else onKeyReleased(keyCode, InputModifiers.current())
	}

	fun onKeyPressed(
		ch: GenericInputButton,
		modifiers: InputModifiers
	): Boolean { // TODO !!!!!: genericify this method to allow for other inputs
		if (!editing) {
			return false
		}
		if (ch == GenericInputButton.escape()) {
			editing = false
			lastPressed = null
			setValue(SavedKeyBinding.unbound())
			updateLabel()
			blur()
			return true
		}
		if (ch.isModifier()) {
			lastPressed = ch
		} else {
			setValue(SavedKeyBinding(ch, modifiers))
			editing = false
			blur()
			lastPressed = null
		}
		updateLabel()
		return true
	}

	fun onLostFocus() {
		editing = false
		lastPressed = null
		updateLabel()
	}

	fun onKeyReleased(ch: GenericInputButton, modifiers: InputModifiers): Boolean {
		if (!editing)
			return false
		if (ch == lastPressed) { // TODO: check modifiers dont duplicate (CTRL+CTRL)
			setValue(SavedKeyBinding(ch, modifiers))
			editing = false
			blur()
			lastPressed = null
		}
		updateLabel()
		return true
	}

	fun updateLabel() {
		var stroke = value().format()
		if (editing) {
			stroke = Component.empty()
			val modifiers = InputModifiers.current()
			if (!modifiers.isEmpty()) {
				stroke.append(modifiers.format())
				stroke.append(" + ")
			}
			stroke.append("???")
			stroke.withStyle { it.withColor(ChatFormatting.YELLOW) }
		}
		label = stroke
	}

	fun createButton(): FirmButtonComponent {
		return object : FirmButtonComponent(
			TextComponent(
				IMinecraft.INSTANCE.defaultFontRenderer,
				{ MoulConfigPlatform.wrap(this@KeyBindingStateManager.label) },
				130,
				TextComponent.TextAlignment.LEFT,
				false,
				false
			), action = {
				this@KeyBindingStateManager.onClick(it)
			}) {
			override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
				if (event is KeyboardEvent.KeyPressed) {
					return this@KeyBindingStateManager.keyboardEvent(
						GenericInputButton.ofKeyAndScan(
							event.keycode,
							event.scancode
						), event.pressed
					)
				}
				return super.keyboardEvent(event, context)
			}

			override fun getBackground(context: GuiImmediateContext): NinePatch<MyResourceLocation> {
				if (this@KeyBindingStateManager.editing) return activeBg
				return super.getBackground(context)
			}


			override fun onLostFocus() {
				this@KeyBindingStateManager.onLostFocus()
			}
		}
	}
}
