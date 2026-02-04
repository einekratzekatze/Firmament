package moe.nea.notfimament.gui.config

import io.github.notenoughupdates.moulconfig.gui.component.TextComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.gui.FirmButtonComponent
import moe.nea.notfimament.jarvis.JarvisIntegration
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.data.ManagedConfig

class HudMetaHandler(
    val config: ManagedConfig,
    val propertyName: String,
    val label: MutableComponent,
    val width: Int,
    val height: Int
) :
	ManagedConfig.OptionHandler<HudMeta> {
	override fun toJson(element: HudMeta): JsonElement? {
		return Json.encodeToJsonElement(element.position)
	}

	override fun fromJson(element: JsonElement): HudMeta {
		return HudMeta(Json.decodeFromJsonElement(element), Firmament.identifier(propertyName), label, width, height)
	}

	fun openEditor(option: ManagedOption<HudMeta>, oldScreen: Screen) {
		MC.screen = JarvisIntegration.jarvis.getHudEditor(
			oldScreen,
			listOf(option.value)
		)
	}

	override fun emitGuiElements(opt: ManagedOption<HudMeta>, guiAppender: GuiAppender) {
		guiAppender.appendLabeledRow(
			opt.labelText,
			FirmButtonComponent(
				TextComponent(
					Component.translatableEscape("firmament.hud.edit", label).string
				),
			) {
				openEditor(opt, guiAppender.screenAccessor())
			})
	}
}
