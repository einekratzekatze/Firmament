package moe.nea.notfimament.features.inventory.buttons

import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.util.ErrorUtil
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.TemplateUtil

object InventoryButtonTemplates {

	val legacyPrefix = "NEUBUTTONS/"
	val modernPrefix = "MAYBEONEDAYIWILLHAVEMYOWNFORMAT"

	fun loadTemplate(t: String): List<InventoryButton>? {
		val buttons = TemplateUtil.maybeDecodeTemplate<List<String>>(legacyPrefix, t) ?: return null
		return buttons.mapNotNull {
			ErrorUtil.catch<InventoryButton?>("Could not import button") {
				Firmament.json.decodeFromString<InventoryButton>(it).also {
					if (it.icon?.startsWith("extra:") == true) {
						MC.sendChat(Component.translatable("firmament.inventory-buttons.import-failed"))
					}
				}
			}.or {
				null
			}
		}
	}

	fun saveTemplate(buttons: List<InventoryButton>): String {
		return TemplateUtil.encodeTemplate(legacyPrefix, buttons.map { Firmament.json.encodeToString(it) })
	}
}
