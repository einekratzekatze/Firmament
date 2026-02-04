package moe.nea.notfimament.gui.config.storage

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class ObjectIndexedJsonPointer(
	val owner: JsonObject,
	val name: String
) : JsonPointer {
	override fun get(): JsonElement {
		return owner.get(name)
	}

	override fun set(value: JsonElement) {
		owner.add(name, value)
	}
}
