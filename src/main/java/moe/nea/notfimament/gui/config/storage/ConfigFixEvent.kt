package moe.nea.notfimament.gui.config.storage

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import moe.nea.notfimament.events.FirmamentEvent
import moe.nea.notfimament.events.FirmamentEventBus

data class ConfigFixEvent(
	val storageClass: ConfigStorageClass,
	val toVersion: Int,
	var data: JsonObject,
) : FirmamentEvent() {
	companion object : FirmamentEventBus<ConfigFixEvent>() {

	}
	fun on(
		toVersion: Int,
		storageClass: ConfigStorageClass,
		block: ConfigEditor.() -> Unit
	) {
		require(toVersion <= FirmamentConfigLoader.currentConfigVersion)
		if (this.toVersion == toVersion && this.storageClass == storageClass) {
			block(ConfigEditor(listOf(object : JsonPointer {
				override fun get(): JsonObject {
					return data
				}

				override fun set(value: JsonElement) {
					data = value as JsonObject
				}

				override fun toString(): String {
					return "ConfigRoot($storageClass)"
				}
			})))
		}
	}
}
