package moe.nea.notfimament.features.world

import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.DefaultSource
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.commands.thenLiteral
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.util.ClipboardUtils
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.tr

object ColeWeightCompat {
	@Serializable
	data class ColeWeightWaypoint(
		val x: Int?,
		val y: Int?,
		val z: Int?,
		val r: Int = 0,
		val g: Int = 0,
		val b: Int = 0,
	)

	fun fromFirm(waypoints: FirmWaypoints, relativeTo: BlockPos): List<ColeWeightWaypoint> {
		return waypoints.waypoints.map {
			ColeWeightWaypoint(it.x - relativeTo.x, it.y - relativeTo.y, it.z - relativeTo.z)
		}
	}

	fun intoFirm(waypoints: List<ColeWeightWaypoint>, relativeTo: BlockPos): FirmWaypoints {
		val w = waypoints
			.filter { it.x != null && it.y != null && it.z != null }
			.map { FirmWaypoints.Waypoint(it.x!! + relativeTo.x, it.y!! + relativeTo.y, it.z!! + relativeTo.z) }
		return FirmWaypoints(
			"Imported Waypoints",
			"imported",
			null,
			w.toMutableList(),
			false
		)
	}

	fun copyAndInform(
        source: DefaultSource,
        origin: BlockPos,
        positiveFeedback: (Int) -> Component,
	) {
		val waypoints = Waypoints.useNonEmptyWaypoints()
			?.let { fromFirm(it, origin) }
		if (waypoints == null) {
			source.sendError(Waypoints.textNothingToExport())
			return
		}
		val data =
			Firmament.tightJson.encodeToString<List<ColeWeightWaypoint>>(waypoints)
		ClipboardUtils.setTextContent(data)
		source.sendFeedback(positiveFeedback(waypoints.size))
	}

	fun importAndInform(
        source: DefaultSource,
        pos: BlockPos?,
        positiveFeedback: (Int) -> Component
	) {
		val text = ClipboardUtils.getTextContents()
		val wr = tryParse(text).map { intoFirm(it, pos ?: BlockPos.ZERO) }
		val waypoints = wr.getOrElse {
			source.sendError(
				tr("firmament.command.waypoint.import.cw.error",
				   "Could not import ColeWeight waypoints."))
			Firmament.logger.error(it)
			return
		}
		waypoints.lastRelativeImport = pos
		Waypoints.waypoints = waypoints
		source.sendFeedback(positiveFeedback(waypoints.size))
	}

	@Subscribe
	fun onEvent(event: CommandEvent.SubCommand) {
		event.subcommand(Waypoints.WAYPOINTS_SUBCOMMAND) {
			thenLiteral("exportcw") {
				thenExecute {
					copyAndInform(source, BlockPos.ZERO) {
						tr("firmament.command.waypoint.export.cw",
						   "Copied $it waypoints to clipboard in ColeWeight format.")
					}
				}
			}
			thenLiteral("exportrelativecw") {
				thenExecute {
					copyAndInform(source, MC.player?.blockPosition() ?: BlockPos.ZERO) {
						tr("firmament.command.waypoint.export.cw.relative",
						   "Copied $it relative waypoints to clipboard in ColeWeight format. Make sure to stand in the same position when importing.")
					}
				}
			}
			thenLiteral("importcw") {
				thenExecute {
					importAndInform(source, null) {
						tr("firmament.command.waypoint.import.cw.success",
							"Imported $it waypoints from ColeWeight.")
					}
				}
			}
			thenLiteral("importrelativecw") {
				thenExecute {
					importAndInform(source, MC.player!!.blockPosition()) {
						tr("firmament.command.waypoint.import.cw.relative",
						   "Imported $it relative waypoints from clipboard. Make sure you stand in the same position as when you exported these waypoints for them to line up correctly.")
					}
				}
			}
		}
	}

	fun tryParse(string: String): Result<List<ColeWeightWaypoint>> {
		return runCatching {
			Firmament.tightJson.decodeFromString<List<ColeWeightWaypoint>>(string)
		}
	}
}
