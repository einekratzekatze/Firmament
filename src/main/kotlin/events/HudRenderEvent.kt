package moe.nea.notfimament.events

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.DeltaTracker
import net.minecraft.world.level.GameType
import moe.nea.notfimament.util.MC

/**
 * Called when hud elements should be rendered, before the screen, but after the world.
 */
data class HudRenderEvent(val context: GuiGraphics, val tickDelta: DeltaTracker) : FirmamentEvent.Cancellable() {
	val isRenderingHud = !MC.options.hideGui
	val isRenderingCursor = MC.interactionManager?.playerMode != GameType.SPECTATOR && isRenderingHud

	init {
		if (!isRenderingHud)
			cancel()
	}

	companion object : FirmamentEventBus<HudRenderEvent>()
}
