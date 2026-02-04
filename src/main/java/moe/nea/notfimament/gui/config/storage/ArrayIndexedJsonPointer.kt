package moe.nea.notfimament.gui.config.storage

import com.google.gson.JsonArray
import com.google.gson.JsonElement

data class ArrayIndexedJsonPointer(
	val owner: JsonArray,
	val index: Int
) : JsonPointer {
	override fun get(): JsonElement {
		return owner.get(index)
	}

	override fun set(value: JsonElement) {
		owner.set(index, value)
	}
}
