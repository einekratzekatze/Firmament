@file:OptIn(ExperimentalSerializationApi::class)

package moe.nea.notfimament.gui.config.storage

import java.nio.file.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.outputStream
import moe.nea.notfimament.Firmament

// TODO: make this class write / read async
class FirstLevelSplitJsonFolder(
	val context: ConfigLoadContext,
	val folder: Path
) {

	var hasCreatedBackup = false

	fun backup(cause: String) {
		if (hasCreatedBackup) return
		hasCreatedBackup = true
		context.createBackup(folder, cause)
	}

	fun load(): JsonObject {
		context.logInfo("Loading FLSJF from $folder")
		if (!folder.exists())
			return JsonObject(mapOf())
		return try {
			folder.listDirectoryEntries("*.json")
				.mapNotNull(::loadIndividualFile)
				.toMap()
				.let(::JsonObject)
				.also { context.logInfo("FLSJF from $folder - Voller Erfolg!") }
		} catch (ex: Exception) {
			context.logError("Could not load files from $folder", ex)
			backup("failed-load")
			JsonObject(mapOf())
		}
	}

	fun loadIndividualFile(path: Path): Pair<String, JsonElement>? {
		context.logDebug("Loading partial file from $path")
		return try {
			path.inputStream().use {
				path.nameWithoutExtension to Firmament.json.decodeFromStream(JsonElement.serializer(), it)
			}
		} catch (ex: Exception) {
			context.logError("Could not load file from $path", ex)
			backup("failed-load")
			null
		}
	}

	fun save(value: JsonObject) {
		context.logInfo("Saving FLSJF to $folder")
		context.logDebug("Current value:\n$value")
		if (!folder.exists()) {
			context.logInfo("Creating folder $folder")
			folder.createDirectories()
		}
		val entries = folder.listDirectoryEntries("*.json")
			.toMutableList()
		for ((name, element) in value) {
			val path = saveIndividualFile(name, element)
			if (path != null) {
				entries.remove(path)
			}
		}
		if (entries.isNotEmpty()) {
			context.logInfo("Deleting additional files.")
			for (path in entries) {
				context.logInfo("Deleting $path")
				backup("save-deletion")
				try {
					path.deleteExisting()
				} catch (ex: Exception) {
					context.logError("Could not delete $path", ex)
				}
			}
		}
		context.logInfo("FLSJF to $folder - Voller Erfolg!")
	}

	fun saveIndividualFile(name: String, element: JsonElement): Path? {
		try {
			context.logDebug("Saving partial file with name $name")
			val path = folder.resolve("$name.json")
			context.ensureWritable(path)
			path.outputStream().use {
				Firmament.json.encodeToStream(JsonElement.serializer(), element, it)
			}
			return path
		} catch (ex: Exception) {
			context.logError("Could not save $name with value $element", ex)
			backup("failed-save")
			return null
		}
	}
}
