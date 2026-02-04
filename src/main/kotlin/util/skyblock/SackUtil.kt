package moe.nea.notfimament.util.skyblock

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Component
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ChestInventoryUpdateEvent
import moe.nea.notfimament.events.ProcessChatEvent
import moe.nea.notfimament.gui.config.storage.ConfigFixEvent
import moe.nea.notfimament.gui.config.storage.ConfigStorageClass
import moe.nea.notfimament.repo.ItemNameLookup
import moe.nea.notfimament.util.SHORT_NUMBER_FORMAT
import moe.nea.notfimament.util.SkyblockId
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ProfileSpecificDataHolder
import moe.nea.notfimament.util.mc.displayNameAccordingToNbt
import moe.nea.notfimament.util.mc.iterableView
import moe.nea.notfimament.util.mc.loreAccordingToNbt
import moe.nea.notfimament.util.parseShortNumber
import moe.nea.notfimament.util.skyBlockId
import moe.nea.notfimament.util.unformattedString
import moe.nea.notfimament.util.useMatch

object SackUtil {
	@Serializable
	data class SackContents(
		// TODO: store the certainty of knowledge for each item.
		val contents: MutableMap<SkyblockId, Long> = mutableMapOf(),
//		val sackTypes:
	)

	@Config
	object Store : ProfileSpecificDataHolder<SackContents>(serializer(), "sacks", ::SackContents)

	@Subscribe
	fun onConfigFix(event: ConfigFixEvent) {
		event.on(996, ConfigStorageClass.PROFILE) {
			move("Sacks", "sacks")
		}
	}

	val items get() = Store.data?.contents ?: mutableMapOf()
	val storedRegex = "^Stored: (?<stored>$SHORT_NUMBER_FORMAT)/(?<max>$SHORT_NUMBER_FORMAT)$".toPattern()

	@Subscribe
	fun storeDataFromInventory(event: ChestInventoryUpdateEvent) {
		val screen = event.inventory as? ContainerScreen ?: return
		if (!screen.title.unformattedString.endsWith(" Sack")) return
		val inv = screen.menu?.container ?: return
		if (inv.containerSize < 18) return
		val backSlot = inv.getItem(inv.containerSize - 5)
		if (backSlot.displayNameAccordingToNbt.unformattedString != "Go Back") return
		if (backSlot.loreAccordingToNbt.map { it.unformattedString } != listOf("To Sack of Sacks")) return
		for (itemStack in inv.iterableView) {
			// TODO: handle runes and gemstones
			val stored = itemStack.loreAccordingToNbt.firstNotNullOfOrNull {
				storedRegex.useMatch(it.unformattedString) {
					val stored = parseShortNumber(group("stored")).toLong()
					val max = parseShortNumber(group("max")).toLong()
					stored
				}
			} ?: continue
			val itemId = itemStack.skyBlockId ?: continue
			items[itemId] = stored
		}
		Store.markDirty()
	}

	@Subscribe
	fun updateFromChat(event: ProcessChatEvent) {
		if (!event.unformattedString.startsWith("[Sacks]")) return
		getUpdatesFromMessage(event.text)
	}

	fun getUpdatesFromMessage(text: Component): List<SackUpdate> {
		val update = ChatUpdate()
		text.siblings.forEach(update::updateFromHoverText)
		return update.updates
	}

	data class SackUpdate(
		val itemId: SkyblockId?,
		val itemName: String,
		val changeAmount: Long,
	)

	private class ChatUpdate {
		val updates = mutableListOf<SackUpdate>()
		var foundAdded = false
		var foundRemoved = false

		fun updateFromCleanText(cleanedText: String) {
			cleanedText.split("\n").forEach { line ->
				changePattern.useMatch(line) {
					val amount = parseShortNumber(group("amount")).toLong()
					val itemName = group("itemName")
					val itemId = ItemNameLookup.guessItemByName(itemName, false)
					updates.add(SackUpdate(itemId, itemName, amount))
				}
			}
		}

		fun updateFromHoverText(text: Component) {
			text.siblings.forEach(::updateFromHoverText)
			val hoverText = (text.style.hoverEvent as? HoverEvent.ShowText)?.value ?: return
			val cleanedText = hoverText.unformattedString
			if (cleanedText.startsWith("Added items:\n")) {
				if (!foundAdded) {
					updateFromCleanText(cleanedText)
					foundAdded = true
				}
			}
			if (cleanedText.startsWith("Removed items:\n")) {
				if (!foundRemoved) {
					updateFromCleanText(cleanedText)
					foundRemoved = true
				}
			}
		}

	}

	val changePattern = "  (?<amount>[+\\-]$SHORT_NUMBER_FORMAT) (?<itemName>[^(]+) \\(.*\\)".toPattern()
}
