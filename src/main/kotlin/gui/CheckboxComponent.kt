package moe.nea.notfimament.gui

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.platform.MoulConfigRenderContext
import net.minecraft.client.renderer.RenderPipelines
import moe.nea.notfimament.Firmament

class CheckboxComponent<T>(
	val state: GetSetter<T>,
	val value: T,
) : GuiComponent() {
	override fun getWidth(): Int {
		return 16
	}

	override fun getHeight(): Int {
		return 16
	}

	fun isEnabled(): Boolean {
		return state.get() == value
	}

	override fun render(context: GuiImmediateContext) {
		val ctx = (context.renderContext as MoulConfigRenderContext).drawContext
		ctx.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			if (isEnabled()) Firmament.identifier("widget/checkbox_checked")
			else Firmament.identifier("widget/checkbox_unchecked"),
			0, 0,
			16, 16
		)
	}

	var isClicking = false

	override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
		if (mouseEvent is MouseEvent.Click) {
			if (isClicking && !mouseEvent.mouseState && mouseEvent.mouseButton == 0) {
				isClicking = false
				if (context.isHovered)
					state.set(value)
				blur()
				return true
			}
			if (mouseEvent.mouseState && mouseEvent.mouseButton == 0 && context.isHovered) {
				requestFocus()
				isClicking = true
				return true
			}
		}
		return false
	}
}
