package moe.nea.notfimament.features.misc

import org.joml.Vector2i
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.HudRenderEvent
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.tr

object Hud {
	val identifier: String
		get() = "hud"

	@Config
	object TConfig : ManagedConfig(identifier, Category.MISC) {
		var dayCount by toggle("day-count") { false }
		val dayCountHud by position("day-count-hud", 80, 10) { Vector2i() }
		var fpsCount by toggle("fps-count") { false }
		val fpsCountHud by position("fps-count-hud", 80, 10) { Vector2i() }
		var pingCount by toggle("ping-count") { false }
		val pingCountHud by position("ping-count-hud", 80, 10) { Vector2i() }
	}

	@Subscribe
	fun onRenderHud(it: HudRenderEvent) {
		if (TConfig.dayCount) {
			it.context.pose().pushMatrix()
			TConfig.dayCountHud.applyTransformations(it.context.pose())
			val day = (MC.world?.dayTime ?: 0L) / 24000
			it.context.drawString(
				MC.font,
				Component.literal(String.format(tr("firmament.config.hud.day-count-hud.display", "Day: %s").string, day)),
				36,
				MC.font.lineHeight,
				-1,
				true
			)
			it.context.pose().popMatrix()
		}

		if (TConfig.fpsCount) {
			it.context.pose().pushMatrix()
			TConfig.fpsCountHud.applyTransformations(it.context.pose())
			it.context.drawString(
				MC.font, Component.literal(
					String.format(
						tr("firmament.config.hud.fps-count-hud.display", "FPS: %s").string, MC.instance.fps
					)
				), 36, MC.font.lineHeight, -1, true
			)
			it.context.pose().popMatrix()
		}

		if (TConfig.pingCount) {
			it.context.pose().pushMatrix()
			TConfig.pingCountHud.applyTransformations(it.context.pose())
			val ping = MC.player?.let {
				val entry: PlayerInfo? = MC.networkHandler?.getPlayerInfo(it.uuid)
				entry?.latency ?: -1
			} ?: -1
			it.context.drawString(
				MC.font, Component.literal(
					String.format(
						tr("firmament.config.hud.ping-count-hud.display", "Ping: %s ms").string, ping
					)
				), 36, MC.font.lineHeight, -1, true
			)

			it.context.pose().popMatrix()
		}
	}
}
