package moe.nea.notfimament.features.debug

import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.commands.thenLiteral
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.events.SoundReceiveEvent
import moe.nea.notfimament.events.WorldReadyEvent
import moe.nea.notfimament.events.WorldRenderLastEvent
import moe.nea.notfimament.util.red
import moe.nea.notfimament.util.render.RenderInWorldContext

object SoundVisualizer {

	var showSounds = false

	var sounds = mutableListOf<SoundReceiveEvent>()


	@Subscribe
	fun onSubCommand(event: CommandEvent.SubCommand) {
		event.subcommand(DeveloperFeatures.DEVELOPER_SUBCOMMAND) {
			thenLiteral("sounds") {
				thenExecute {
					showSounds = !showSounds
					if (!showSounds) {
						sounds.clear()
					}
				}
			}
		}
	}

	@Subscribe
	fun onWorldSwap(event: WorldReadyEvent) {
		sounds.clear()
	}

	@Subscribe
	fun onRender(event: WorldRenderLastEvent) {
		RenderInWorldContext.renderInWorld(event) {
			sounds.forEach { event ->
				withFacingThePlayer(event.position) {
					text(
						Component.literal(event.sound.value().location.toString()).also {
							if (event.cancelled)
								it.red()
						},
						verticalAlign = RenderInWorldContext.VerticalAlign.CENTER,
					)
				}
			}
		}
	}

	@Subscribe
	fun onSoundReceive(event: SoundReceiveEvent) {
		if (!showSounds) return
		if (sounds.size > 1000) {
			sounds.subList(0, 200).clear()
		}
		sounds.add(event)
	}
}
