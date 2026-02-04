package moe.nea.notfimament.features.events.anniversity

import java.util.Optional
import me.shedaniel.math.Color
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Style
import net.minecraft.ChatFormatting
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.EntityRenderTintEvent
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.SkyblockId
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.render.TintedOverlayTexture
import moe.nea.notfimament.util.skyBlockId
import moe.nea.notfimament.util.skyblock.SkyBlockItems

object CenturyRaffleFeatures {
	@Config
	object TConfig : ManagedConfig("centuryraffle", Category.EVENTS) {
		val highlightPlayersForSlice by toggle("highlight-cake-players") { true }
//		val highlightAllPlayers by toggle("highlight-all-cake-players") { true }
	}

	val cakeIcon = "⛃"

	val cakeColors = listOf(
		CakeTeam(SkyBlockItems.SLICE_OF_BLUEBERRY_CAKE, ChatFormatting.BLUE),
		CakeTeam(SkyBlockItems.SLICE_OF_CHEESECAKE, ChatFormatting.YELLOW),
		CakeTeam(SkyBlockItems.SLICE_OF_GREEN_VELVET_CAKE, ChatFormatting.GREEN),
		CakeTeam(SkyBlockItems.SLICE_OF_RED_VELVET_CAKE, ChatFormatting.RED),
		CakeTeam(SkyBlockItems.SLICE_OF_STRAWBERRY_SHORTCAKE, ChatFormatting.LIGHT_PURPLE),
	)

	data class CakeTeam(
        val id: SkyblockId,
        val formatting: ChatFormatting,
	) {
		val searchedTextRgb = formatting.color!!
		val brightenedRgb = Color.ofOpaque(searchedTextRgb)//.brighter(2.0)
		val tintOverlay by lazy {
			TintedOverlayTexture().setColor(brightenedRgb)
		}
	}

	val sliceToColor = cakeColors.associateBy { it.id }

	@Subscribe
	fun onEntityRender(event: EntityRenderTintEvent) {
		if (!TConfig.highlightPlayersForSlice) return
		val requestedCakeTeam = sliceToColor[MC.stackInHand?.skyBlockId] ?: return
		// TODO: cache the requested color
		val player = event.entity as? Player ?: return
		val cakeColor: Style = player.feedbackDisplayName.visit(
			{ style, text ->
				if (text == cakeIcon) Optional.of(style)
				else Optional.empty()
			}, Style.EMPTY).getOrNull() ?: return
		if (cakeColor.color?.value == requestedCakeTeam.searchedTextRgb) {
			event.renderState.overlayTexture_firmament = requestedCakeTeam.tintOverlay
		}
	}

}
