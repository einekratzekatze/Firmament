package moe.nea.notfimament.features.inventory

import org.lwjgl.glfw.GLFW
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ItemTooltipEvent
import moe.nea.notfimament.repo.HypixelStaticData
import moe.nea.notfimament.util.FirmFormatters.formatCommas
import moe.nea.notfimament.util.asBazaarStock
import moe.nea.notfimament.util.bold
import moe.nea.notfimament.util.darkGrey
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.getLogicalStackSize
import moe.nea.notfimament.util.gold
import moe.nea.notfimament.util.skyBlockId
import moe.nea.notfimament.util.tr
import moe.nea.notfimament.util.yellow

object PriceData {
	val identifier: String
		get() = "price-data"

	@Config
	object TConfig : ManagedConfig(identifier, Category.INVENTORY) {
		val tooltipEnabled by toggle("enable-always") { true }
		val enableKeybinding by keyBindingWithDefaultUnbound("enable-keybind")
		val stackSizeKey by keyBinding("stack-size-keybind") { GLFW.GLFW_KEY_LEFT_SHIFT }
		val avgLowestBin by choice(
			"avg-lowest-bin-days",
		) {
			AvgLowestBin.THREEDAYAVGLOWESTBIN
		}
	}

	enum class AvgLowestBin : StringRepresentable {
		OFF,
		ONEDAYAVGLOWESTBIN,
		THREEDAYAVGLOWESTBIN,
		SEVENDAYAVGLOWESTBIN;

		override fun getSerializedName(): String {
			return name
		}
	}

	fun formatPrice(label: Component, price: Double): Component {
		return Component.literal("")
			.yellow()
			.bold()
			.append(label)
			.append(": ")
			.append(
				Component.literal(formatCommas(price, fractionalDigits = 1))
					.append(if (price != 1.0) " coins" else " coin")
					.gold()
					.bold()
			)
	}

	@Subscribe
	fun onItemTooltip(it: ItemTooltipEvent) {
		if (!TConfig.tooltipEnabled) return
		if (TConfig.enableKeybinding.isBound && !TConfig.enableKeybinding.isPressed()) return
		val sbId = it.stack.skyBlockId
		val stackSize = it.stack.getLogicalStackSize()
		val isShowingStack = TConfig.stackSizeKey.isPressed()
		val multiplier = if (isShowingStack) stackSize else 1
		val multiplierText =
			if (isShowingStack)
				tr("firmament.tooltip.multiply", "Showing prices for x${stackSize}").darkGrey()
			else
				tr(
					"firmament.tooltip.multiply.hint",
					"[${TConfig.stackSizeKey.format()}] to show x${stackSize}"
				).darkGrey()
		val bazaarData = HypixelStaticData.bazaarData[sbId?.asBazaarStock]
		val lowestBin = HypixelStaticData.lowestBin[sbId]
		val avgBinValue: Double? = when (TConfig.avgLowestBin) {
			AvgLowestBin.ONEDAYAVGLOWESTBIN -> HypixelStaticData.avg1dlowestBin[sbId]
			AvgLowestBin.THREEDAYAVGLOWESTBIN -> HypixelStaticData.avg3dlowestBin[sbId]
			AvgLowestBin.SEVENDAYAVGLOWESTBIN -> HypixelStaticData.avg7dlowestBin[sbId]
			AvgLowestBin.OFF -> null
		}
		if (bazaarData != null) {
			it.lines.add(Component.literal(""))
			it.lines.add(multiplierText)
			it.lines.add(
				formatPrice(
					tr("firmament.tooltip.bazaar.buy-order", "Bazaar Buy Order"),
					bazaarData.quickStatus.sellPrice * multiplier
				)
			)
			it.lines.add(
				formatPrice(
					tr("firmament.tooltip.bazaar.sell-order", "Bazaar Sell Order"),
					bazaarData.quickStatus.buyPrice * multiplier
				)
			)
		} else if (lowestBin != null) {
			it.lines.add(Component.literal(""))
			it.lines.add(multiplierText)
			it.lines.add(
				formatPrice(
					tr("firmament.tooltip.ah.lowestbin", "Lowest BIN"),
					lowestBin * multiplier
				)
			)
			if (avgBinValue != null) {
				it.lines.add(
					formatPrice(
						tr("firmament.tooltip.ah.avg-lowestbin", "AVG Lowest BIN"),
						avgBinValue * multiplier
					)
				)
			}
		}
	}
}
