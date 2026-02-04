package moe.nea.notfimament.features.mining

import io.github.notenoughupdates.moulconfig.xml.Bind
import org.joml.Vector2i
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.time.Duration.Companion.seconds
import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ProcessChatEvent
import moe.nea.notfimament.gui.hud.MoulConfigHud
import moe.nea.notfimament.util.BazaarPriceStrategy
import moe.nea.notfimament.util.FirmFormatters.formatCommas
import moe.nea.notfimament.util.SkyblockId
import moe.nea.notfimament.util.StringUtil.parseIntWithComma
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.data.ProfileSpecificDataHolder
import moe.nea.notfimament.util.formattedString
import moe.nea.notfimament.util.useMatch

object PristineProfitTracker {
	val identifier: String
		get() = "pristine-profit"

	enum class GemstoneKind(
		val label: String,
	) {
		SAPPHIRE("Sapphire"),
		RUBY("Ruby"),
		AMETHYST("Amethyst"),
		AMBER("Amber"),
		TOPAZ("Topaz"),
		JADE("Jade"),
		JASPER("Jasper"),
		OPAL("Opal"),
		PERIDOT("Peridot"),
		ONXY("Onyx"),
		AQUAMARINE("Aquamarine"),
		CITRINE("Citrine"),
		;

		val flawedId: SkyblockId = SkyblockId("FLAWED_${name}_GEM")
		val fineId: SkyblockId = SkyblockId("FINE_${name}_GEM")
	}

	@Serializable
	data class Data(
		var maxMoneyPerSecond: Double = 1.0,
		var maxCollectionPerSecond: Double = 1.0,
	)

	@Config
	object DConfig : ProfileSpecificDataHolder<Data>(serializer(), identifier, ::Data)

	@Config
	object TConfig : ManagedConfig(identifier, Category.MINING) {
		val timeout by duration("timeout", 0.seconds, 120.seconds) { 30.seconds }
		val gui by position("position", 100, 30) { Vector2i() }
		val useFineGemstones by toggle("fine-gemstones") { false }
	}

	val sellingStrategy = BazaarPriceStrategy.SELL_ORDER

	val pristineRegex =
		"PRISTINE! You found . Flawed (?<kind>${
			GemstoneKind.entries.joinToString("|") { it.label }
		}) Gemstone x(?<count>[0-9,]+)!".toPattern()

	val collectionHistogram = Histogram<Double>(10000, 180.seconds)

	/**
	 * Separate histogram for money, since money changes based on gemstone, therefore we cannot calculate money from collection.
	 */
	val moneyHistogram = Histogram<Double>(10000, 180.seconds)

	object ProfitHud : MoulConfigHud("pristine_profit", TConfig.gui) {
		@field:Bind
		var moneyCurrent: Double = 0.0

		@field:Bind
		var moneyMax: Double = 1.0

		@field:Bind
		var moneyText = ""

		@field:Bind
		var collectionCurrent = 0.0

		@field:Bind
		var collectionMax = 1.0

		@field:Bind
		var collectionText = ""
		override fun shouldRender(): Boolean = collectionHistogram.latestUpdate().passedTime() < TConfig.timeout
	}

	val SECONDS_PER_HOUR = 3600
	val ROUGHS_PER_FLAWED = 80
	val FLAWED_PER_FINE = 80
	val ROUGHS_PER_FINE = ROUGHS_PER_FLAWED * FLAWED_PER_FINE

	fun updateUi() {
		val collectionPerSecond = collectionHistogram.averagePer({ it }, 1.seconds)
		val moneyPerSecond = moneyHistogram.averagePer({ it }, 1.seconds)
		if (collectionPerSecond == null || moneyPerSecond == null) return
		ProfitHud.collectionCurrent = collectionPerSecond
		ProfitHud.collectionText = Component.translatableEscape("firmament.pristine-profit.collection",
		                                                        formatCommas(collectionPerSecond * SECONDS_PER_HOUR,
		                                                                     1)).formattedString()
		ProfitHud.moneyCurrent = moneyPerSecond
		ProfitHud.moneyText = Component.translatableEscape("firmament.pristine-profit.money",
		                                                   formatCommas(moneyPerSecond * SECONDS_PER_HOUR, 1))
			.formattedString()
		val data = DConfig.data
		if (data.maxCollectionPerSecond < collectionPerSecond && collectionHistogram.oldestUpdate()
				.passedTime() > 30.seconds
		) {
			data.maxCollectionPerSecond = collectionPerSecond
			DConfig.markDirty()
		}
		if (data.maxMoneyPerSecond < moneyPerSecond && moneyHistogram.oldestUpdate().passedTime() > 30.seconds) {
			data.maxMoneyPerSecond = moneyPerSecond
			DConfig.markDirty()
		}
		ProfitHud.collectionMax = maxOf(data.maxCollectionPerSecond, collectionPerSecond)
		ProfitHud.moneyMax = maxOf(data.maxMoneyPerSecond, moneyPerSecond)
	}


	@Subscribe
	fun onMessage(it: ProcessChatEvent) {
		pristineRegex.useMatch(it.unformattedString) {
			val gemstoneKind = GemstoneKind.valueOf(group("kind").uppercase())
			val flawedCount = parseIntWithComma(group("count"))
			val moneyAmount =
				if (TConfig.useFineGemstones) sellingStrategy.getSellPrice(gemstoneKind.fineId) * flawedCount / FLAWED_PER_FINE
				else sellingStrategy.getSellPrice(gemstoneKind.flawedId) * flawedCount
			moneyHistogram.record(moneyAmount)
			val collectionAmount = flawedCount * ROUGHS_PER_FLAWED
			collectionHistogram.record(collectionAmount.toDouble())
			updateUi()
		}
	}
}
