package moe.nea.notfimament.features.texturepack

import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.network.chat.Component
import net.minecraft.util.profiling.ProfilerFiller
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.FinalizeResourceManagerEvent
import moe.nea.notfimament.util.ErrorUtil.intoCatch

object CustomTextReplacements : SimplePreparableReloadListener<List<TreeishTextReplacer>>() {

	override fun prepare(
        manager: ResourceManager,
        profiler: ProfilerFiller
	): List<TreeishTextReplacer> {
		return manager.listResources("overrides/texts") { it.namespace == "firmskyblock" && it.path.endsWith(".json") }
			.mapNotNull {
				Firmament.tryDecodeJsonFromStream<TreeishTextReplacer>(it.value.open())
					.intoCatch("Failed to load text override from ${it.key}").orNull()
			}
	}

	var textReplacers: List<TreeishTextReplacer> = listOf()

	override fun apply(
        prepared: List<TreeishTextReplacer>,
        manager: ResourceManager,
        profiler: ProfilerFiller
	) {
		this.textReplacers = prepared
	}

	@JvmStatic
	fun replaceTexts(texts: List<Component>): List<Component> {
		return texts.map { replaceText(it) }
	}

	@JvmStatic
	fun replaceText(text: Component): Component {
		// TODO: add a config option for this
		val rawText = text.string
		var text = text
		for (replacer in textReplacers) {
			if (!replacer.match.matches(rawText)) continue
			text = replacer.replaceText(text)
		}
		return text
	}

	@Subscribe
	fun onReloadStart(event: FinalizeResourceManagerEvent) {
		event.resourceManager.registerReloadListener(this)
	}
}
