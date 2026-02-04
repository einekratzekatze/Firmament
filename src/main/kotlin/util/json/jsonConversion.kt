package moe.nea.notfimament.util.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.internal.LazilyParsedNumber


fun JsonElement.intoKotlinJson(): kotlinx.serialization.json.JsonElement {
	when (this) {
		is JsonNull -> return kotlinx.serialization.json.JsonNull
		is JsonObject -> {
			return kotlinx.serialization.json.JsonObject(
				this.entrySet()
					.associate { it.key to it.value.intoKotlinJson() })
		}

		is JsonArray -> {
			return kotlinx.serialization.json.JsonArray(this.map { it.intoKotlinJson() })
		}

		is JsonPrimitive -> {
			if (this.isString)
				return kotlinx.serialization.json.JsonPrimitive(this.asString)
			if (this.isBoolean)
				return kotlinx.serialization.json.JsonPrimitive(this.asBoolean)
			return kotlinx.serialization.json.JsonPrimitive(this.asNumber)
		}

		else -> error("Unknown json variant $this")
	}
}

fun kotlinx.serialization.json.JsonElement.intoGson(): JsonElement {
	when (this) {
		is kotlinx.serialization.json.JsonNull -> return JsonNull.INSTANCE
		is kotlinx.serialization.json.JsonPrimitive -> {
			if (this.isString)
				return JsonPrimitive(this.content)
			if (this.content == "true")
				return JsonPrimitive(true)
			if (this.content == "false")
				return JsonPrimitive(false)
			return JsonPrimitive(LazilyParsedNumber(this.content))
		}

		is kotlinx.serialization.json.JsonObject -> {
			val obj = JsonObject()
			for ((k, v) in this) {
				obj.add(k, v.intoGson())
			}
			return obj
		}

		is kotlinx.serialization.json.JsonArray -> {
			val arr = JsonArray()
			for (v in this) {
				arr.add(v.intoGson())
			}
			return arr
		}
	}
}
