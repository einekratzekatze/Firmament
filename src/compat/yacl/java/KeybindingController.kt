package moe.nea.notfimament.compat.yacl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.ControllerWidget
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import moe.nea.notfimament.gui.config.KeyBindingHandler
import moe.nea.notfimament.gui.config.KeyBindingStateManager
import moe.nea.notfimament.gui.config.ManagedOption
import moe.nea.notfimament.keybindings.GenericInputButton
import moe.nea.notfimament.keybindings.SavedKeyBinding

class KeybindingController(
	val option: Option<SavedKeyBinding>,
	val managedOption: ManagedOption<SavedKeyBinding>,
) : Controller<SavedKeyBinding> {
	val handler = managedOption.handler as KeyBindingHandler
	override fun option(): Option<SavedKeyBinding> {
		return option
	}

	override fun formatValue(): Component {
		return option.pendingValue().format()
	}

	override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
		lateinit var button: ControllerWidget<KeybindingController>
		val sm = KeyBindingStateManager(
			{ option.pendingValue() },
			{ option.requestSet(it) },
			{ screen.focused = null },
			{ screen.focused = button },
		)
		button = KeybindingWidget(sm, this, screen, widgetDimension)
		option.addListener { t, u ->
			sm.updateLabel()
		}
		sm.updateLabel()
		return button
	}
}

class KeybindingWidget(
	val sm: KeyBindingStateManager,
	controller: KeybindingController,
	screen: YACLScreen,
	dimension: Dimension<Int>
) : ControllerWidget<KeybindingController>(controller, screen, dimension) {
	override fun getHoveredControlWidth(): Int {
		return 130
	}

	override fun getValueText(): Component {
		return sm.label
	}

	override fun keyPressed(keyEvent: KeyEvent): Boolean {
		return sm.keyboardEvent(GenericInputButton.of(keyEvent), true)
	}

	override fun keyReleased(keyEvent: KeyEvent): Boolean {
		return sm.keyboardEvent(GenericInputButton.of(keyEvent), false)
	}

	override fun unfocus() {
		sm.onLostFocus()
	}

	override fun setFocused(focused: Boolean) {
		super.setFocused(focused)
		if (!focused) sm.onLostFocus()
	}

	override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (isHovered) {
			sm.onClick(mouseButtonEvent.button())
			return true
		}
		return super.mouseClicked(mouseButtonEvent, doubleClick)
	}
}
