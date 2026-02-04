package moe.nea.notfimament.util

import java.util.Optional
import net.minecraft.client.gui.Gui
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.TickEvent

object ScoreboardUtil {
	var scoreboardLines: List<Component> = listOf()
	var simplifiedScoreboardLines: List<String> = listOf()

	@Subscribe
	fun onTick(event: TickEvent) {
		scoreboardLines = getScoreboardLinesUncached()
		simplifiedScoreboardLines = scoreboardLines.map { it.unformattedString }
	}

	private fun getScoreboardLinesUncached(): List<Component> {
		val scoreboard = MC.instance.level?.scoreboard ?: return listOf()
		val activeObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return listOf()
		return scoreboard.listPlayerScores(activeObjective)
			.filter { !it.isHidden() }
			.sortedWith(Gui.SCORE_DISPLAY_ORDER)
			.take(15).map {
				val team = scoreboard.getPlayersTeam(it.owner)
				val text = it.ownerName()
				PlayerTeam.formatNameForTeam(team, text)
			}
	}
}

fun Component.formattedString(): String {
	val sb = StringBuilder()
	visit(FormattedText.StyledContentConsumer<Unit> { style, string ->
		val c = ChatFormatting.getByName(style.color?.serialize())
		if (c != null) {
			sb.append("§${c.char}")
		}
		if (style.isUnderlined) {
			sb.append("§n")
		}
		if (style.isBold) {
			sb.append("§l")
		}
		sb.append(string)
		Optional.empty()
	}, Style.EMPTY)
	return sb.toString().replace("§[^a-f0-9]".toRegex(), "")
}
