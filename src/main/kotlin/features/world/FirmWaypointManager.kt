package moe.nea.notfimament.features.world

import com.mojang.brigadier.arguments.StringArgumentType
import kotlinx.serialization.serializer
import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.DefaultSource
import moe.nea.notfimament.commands.RestArgumentType
import moe.nea.notfimament.commands.get
import moe.nea.notfimament.commands.suggestsList
import moe.nea.notfimament.commands.thenArgument
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.commands.thenLiteral
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.util.ClipboardUtils
import moe.nea.notfimament.util.FirmFormatters
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.TemplateUtil
import moe.nea.notfimament.util.data.DataHolder
import moe.nea.notfimament.util.tr

object FirmWaypointManager {
	object DConfig : DataHolder<MutableMap<String, FirmWaypoints>>(serializer(), "waypoints", ::mutableMapOf)

	val SHARE_PREFIX = "FIRM_WAYPOINTS/"
	val ENCODED_SHARE_PREFIX = TemplateUtil.getPrefixComparisonSafeBase64Encoding(SHARE_PREFIX)

	fun createExportableCopy(
		waypoints: FirmWaypoints,
	): FirmWaypoints {
		val copy = waypoints.copy(waypoints = waypoints.waypoints.toMutableList())
		if (waypoints.isRelativeTo != null) {
			val origin = waypoints.lastRelativeImport
			if (origin != null) {
				copy.waypoints.replaceAll {
					it.copy(
						x = it.x - origin.x,
						y = it.y - origin.y,
						z = it.z - origin.z,
					)
				}
			} else {
				TODO("Add warning!")
			}
		}
		return copy
	}

	fun loadWaypoints(waypoints: FirmWaypoints, sendFeedback: (Component) -> Unit) {
		val copy = waypoints.deepCopy()
		if (copy.isRelativeTo != null) {
			val origin = MC.player!!.blockPosition()
			copy.waypoints.replaceAll {
				it.copy(
					x = it.x + origin.x,
					y = it.y + origin.y,
					z = it.z + origin.z,
				)
			}
			copy.lastRelativeImport = origin.immutable()
			sendFeedback(tr("firmament.command.waypoint.import.ordered.success",
			                "Imported ${copy.size} relative waypoints. Make sure you stand in the correct spot while loading the waypoints: ${copy.isRelativeTo}."))
		} else {
			sendFeedback(tr("firmament.command.waypoint.import.success",
			                "Imported ${copy.size} waypoints."))
		}
		Waypoints.waypoints = copy
	}

	fun setOrigin(source: DefaultSource, text: String?) {
		val waypoints = Waypoints.useEditableWaypoints()
		waypoints.isRelativeTo = text ?: waypoints.isRelativeTo ?: ""
		val pos = MC.player!!.blockPosition()
		waypoints.lastRelativeImport = pos
		source.sendFeedback(tr("firmament.command.waypoint.originset",
		                       "Set the origin of waypoints to ${FirmFormatters.formatPosition(pos)}. Run /firm waypoints export to save the waypoints relative to this position."))
	}

	@Subscribe
	fun onCommands(event: CommandEvent.SubCommand) {
		event.subcommand(Waypoints.WAYPOINTS_SUBCOMMAND) {
			thenLiteral("setorigin") {
				thenExecute {
					setOrigin(source, null)
				}
				thenArgument("hint", RestArgumentType) { text ->
					thenExecute {
						setOrigin(source, this[text])
					}
				}
			}
			thenLiteral("clearorigin") {
				thenExecute {
					val waypoints = Waypoints.useEditableWaypoints()
					waypoints.lastRelativeImport = null
					waypoints.isRelativeTo = null
					source.sendFeedback(tr("firmament.command.waypoint.originunset",
					                       "Unset the origin of the waypoints. Run /firm waypoints export to save the waypoints with absolute coordinates."))
				}
			}
			thenLiteral("save") {
				thenArgument("name", StringArgumentType.string()) { name ->
					suggestsList { DConfig.data.keys }
					thenExecute {
						val waypoints = Waypoints.useNonEmptyWaypoints()
						if (waypoints == null) {
							source.sendError(Waypoints.textNothingToExport())
							return@thenExecute
						}
						waypoints.id = get(name)
						val exportableWaypoints = createExportableCopy(waypoints)
						DConfig.data[get(name)] = exportableWaypoints
						DConfig.markDirty()
						source.sendFeedback(tr("firmament.command.waypoint.saved",
						                       "Saved waypoints locally as ${get(name)}. Use /firm waypoints load to load them again."))
					}
				}
			}
			thenLiteral("load") {
				thenArgument("name", StringArgumentType.string()) { name ->
					suggestsList { DConfig.data.keys }
					thenExecute {
						val name = get(name)
						val waypoints = DConfig.data[name]
						if (waypoints == null) {
							source.sendError(
								tr("firmament.command.waypoint.nosaved",
								   "No saved waypoint for ${name}. Use tab completion to see available names."))
							return@thenExecute
						}
						loadWaypoints(waypoints, source::sendFeedback)
					}
				}
			}
			thenLiteral("export") {
				thenExecute {
					val waypoints = Waypoints.useNonEmptyWaypoints()
					if (waypoints == null) {
						source.sendError(Waypoints.textNothingToExport())
						return@thenExecute
					}
					val exportableWaypoints = createExportableCopy(waypoints)
					val data = TemplateUtil.encodeTemplate(SHARE_PREFIX, exportableWaypoints)
					ClipboardUtils.setTextContent(data)
					source.sendFeedback(tr("firmament.command.waypoint.export",
					                       "Copied ${exportableWaypoints.size} waypoints to clipboard in Firmament format."))
				}
			}
			thenLiteral("import") {
				thenExecute {
					val text = ClipboardUtils.getTextContents()
					if (text.startsWith("[")) {
						source.sendError(tr("firmament.command.waypoint.import.lookslikecw",
						                    "The waypoints in your clipboard look like they might be ColeWeight waypoints. If so, use /firm waypoints importcw or /firm waypoints importrelativecw."))
						return@thenExecute
					}
					val waypoints = TemplateUtil.maybeDecodeTemplate<FirmWaypoints>(SHARE_PREFIX, text)
					if (waypoints == null) {
						source.sendError(tr("firmament.command.waypoint.import.error",
						                    "Could not import Firmament waypoints from your clipboard. Make sure they are Firmament compatible waypoints."))
						return@thenExecute
					}
					loadWaypoints(waypoints, source::sendFeedback)
				}
			}
		}
	}
}
