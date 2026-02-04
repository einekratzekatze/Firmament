package moe.nea.notfimament.compat.moulconfig

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import io.github.notenoughupdates.moulconfig.platform.MoulConfigPlatform
import moe.nea.notfimament.gui.config.ManagedOption
import moe.nea.notfimament.util.ErrorUtil

abstract class ProcessedEditableOptionFirm<T : Any>(
	val managedOption: ManagedOption<T>,
	categoryAccordionId: Int,
	configObject: Config,
) : ProcessedOptionFirm(categoryAccordionId, configObject) {
	val managedConfig = managedOption.element
	override fun getDebugDeclarationLocation(): String {
		return "FirmamentOption:${managedConfig.name}:${managedOption.propertyName}"
	}

	override fun getName(): StructuredText {
		return MoulConfigPlatform.wrap(managedOption.labelText)
	}

	override fun getDescription(): StructuredText {
		return MoulConfigPlatform.wrap(managedOption.labelDescription)
	}

	abstract fun fromT(t: T): Any
	abstract fun toT(any: Any?): T?

	final override fun get(): Any {
		return fromT(managedOption.value)
	}

	final override fun set(p0: Any?): Boolean {
		managedOption.value = toT(p0) ?: run {
			ErrorUtil.softError("Failed to set value p0 in $this")
			return false
		}
		managedConfig.markDirty()
		return true
	}

	override fun explicitNotifyChange() {
		managedConfig.markDirty()
	}
}
