package moe.nea.notfimament.gui.config.storage

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.serialization.json.JsonElement
import moe.nea.notfimament.util.json.intoGson
import moe.nea.notfimament.util.json.intoKotlinJson

data class ConfigEditor(
	val roots: List<JsonPointer>,
) {
	fun transform(transform: (JsonElement) -> JsonElement) {
		roots.forEach { root ->
			root.set(transform(root.get().intoKotlinJson()).intoGson())
		}
	}

	fun move(fromPath: String, toPath: String) {
		if (fromPath == toPath) return
		val fromSegments = fromPath.split(".").filter { it.isNotEmpty() }
		val toSegments = toPath.split(".").filter { it.isNotEmpty() }
		roots.forEach { root ->
			var fp = root.get()
			if (fromSegments.isEmpty()) {
				root.set(JsonObject())
			} else {
				fromSegments.dropLast(1).forEach {
					fp = (fp as JsonObject)[it] ?: return@forEach // todo warn if we dont find the object maybe
				}
				fp as JsonObject
				fp = fp.remove(fromSegments.last())?.deepCopy() ?: return@forEach // in theory i don't need to deepcopy but fuck theory
			}
			if (toSegments.isEmpty()) {
				root.set(fp)
			} else {
				var lp = root.get()
				toSegments.dropLast(1).forEach { name ->
					val parent = lp as JsonObject
					var child = parent[name]
					if (child == null) {
						child = JsonObject()
						parent.add(name, child)
					}
					lp = child
				}
				lp as JsonObject
				if (lp.has(toSegments.last())) {
					error("Cannot overwrite $lp.${toSegments.last()} with $fp")
				}
				lp.add(toSegments.last(), fp)
			}
		}
	}

	fun at(path: String, block: ConfigEditor.() -> Unit) {
		block(at(path))
	}

	fun at(path: String): ConfigEditor {
		var lastRoots = roots
		for (segment in path.split(".")) {
			if (segment.isEmpty()) {
				continue
			} else if (segment == "*") {
				lastRoots = lastRoots.flatMap { root ->
					when (val ele = root.get()) {
						is JsonObject -> {
							ele.entrySet().map {
								(ObjectIndexedJsonPointer(ele, it.key))
							}
						}

						is JsonArray -> {
							(0..<ele.size()).map {
								(ArrayIndexedJsonPointer(ele, it))
							}
						}

						else -> {
							error("Cannot expand a json primitive $ele at $path")
						}
					}
				}
			} else {
				lastRoots = lastRoots.map { root ->
					when (val ele = root.get()) {
						is JsonObject -> {
							ObjectIndexedJsonPointer(ele, segment)
						}

						is JsonArray -> {
							ArrayIndexedJsonPointer(ele, segment.toInt())
						}

						else -> {
							error("Cannot expand a json primitive $ele at $path")
						}
					}
				}
			}
		}
		return ConfigEditor(lastRoots)
	}
}
