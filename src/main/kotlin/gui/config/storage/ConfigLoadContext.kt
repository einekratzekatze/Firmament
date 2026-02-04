package moe.nea.notfimament.gui.config.storage

import java.io.PrintWriter
import java.nio.file.Path
import org.apache.commons.io.output.StringBuilderWriter
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.OnErrorResult
import kotlin.io.path.Path
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText
import moe.nea.notfimament.Firmament

data class ConfigLoadContext(
	val loadId: String,
) : AutoCloseable {
	val backupPath = Path("backups").resolve(Firmament.MOD_ID)
		.resolve("config-$loadId")
		.toAbsolutePath()
	val logFile = Path("logs")
		.resolve(Firmament.MOD_ID)
		.resolve("config-$loadId.log")
		.toAbsolutePath()
	val logBuffer = StringBuilder()

	var shouldSaveLogBuffer = false
	fun markShouldSaveLogBuffer() {
		shouldSaveLogBuffer = true
	}

	fun logDebug(message: String) {
		logBuffer.append("[DEBUG] ").append(message).appendLine()
	}

	fun logInfo(message: String) {
		if (Firmament.DEBUG)
			Firmament.logger.info("[ConfigUpgrade] $message")
		logBuffer.append("[INFO] ").append(message).appendLine()
	}

	fun logError(message: String, exception: Throwable) {
		markShouldSaveLogBuffer()
		if (Firmament.DEBUG)
			Firmament.logger.error("[ConfigUpgrade] $message", exception)
		logBuffer.append("[ERROR] ").append(message).appendLine()
		PrintWriter(StringBuilderWriter(logBuffer)).use {
			exception.printStackTrace(it)
		}
		logBuffer.appendLine()
	}

	fun logError(message: String) {
		markShouldSaveLogBuffer()
		Firmament.logger.error("[ConfigUpgrade] $message")
		logBuffer.append("[ERROR] ").append(message).appendLine()
	}

	fun ensureWritable(path: Path) {
		path.createParentDirectories()
	}

	fun use(block: (ConfigLoadContext) -> Unit) {
		try {
			block(this)
		} catch (ex: Exception) {
			logError("Caught exception on CLC", ex)
		} finally {
			close()
		}
	}

	override fun close() {
		logInfo("Closing out config load.")
		if (shouldSaveLogBuffer) {
			try {
				ensureWritable(logFile)
				logFile.writeText(logBuffer.toString())
			} catch (ex: Exception) {
				logError("Could not save config load log", ex)
			}
		}
	}

	@OptIn(ExperimentalPathApi::class)
	fun createBackup(folder: Path, string: String) {
		val backupDestination = backupPath.resolve("$string-${System.currentTimeMillis()}")
		logError("Creating backup of $folder in $backupDestination")
		folder.copyToRecursively(
			backupDestination.createParentDirectories(),
			onError = { source: Path, target: Path, exception: Exception ->
				logError("Failed to copy subtree $source to $target", exception)
				OnErrorResult.SKIP_SUBTREE
			},
			followLinks = false,
			overwrite = false
		)
	}
}
