package moe.nea.notfimament.repo

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipInputStream
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.io.path.readText
import kotlin.io.path.writeText
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.Firmament.logger
import moe.nea.notfimament.repo.RepoDownloadManager.latestSavedVersionHash
import moe.nea.notfimament.util.iterate
import moe.nea.notfimament.util.net.HttpUtil


object RepoDownloadManager {

	val repoSavedLocation = Firmament.DATA_DIR.resolve("repo-extracted")
	val repoMetadataLocation = Firmament.DATA_DIR.resolve("loaded-repo-sha.txt")

	private fun loadSavedVersionHash(): String? =
		if (repoSavedLocation.exists()) {
			if (repoMetadataLocation.exists()) {
				try {
					repoMetadataLocation.readText().trim()
				} catch (e: IOException) {
					null
				}
			} else {
				null
			}
		} else null

	private fun saveVersionHash(versionHash: String) {
		latestSavedVersionHash = versionHash
		repoMetadataLocation.writeText(versionHash)
	}

	var latestSavedVersionHash: String? = loadSavedVersionHash()
		private set

	@Serializable
	private class GithubCommitsResponse(val sha: String)

	private suspend fun requestLatestGithubSha(branchOverride: String?): String? {
		if (RepoManager.TConfig.branch == "prerelease") {
			RepoManager.TConfig.branch = "master"
		}
		val response =
			HttpUtil.request("https://api.github.com/repos/${RepoManager.TConfig.username}/${RepoManager.TConfig.reponame}/commits/${branchOverride ?: RepoManager.TConfig.branch}")
				.forJson<GithubCommitsResponse>()
				.await()
		return response.sha
	}

	private suspend fun downloadGithubArchive(url: String): Path = withContext(IO) {
		val response = HttpUtil.request(url)
		val targetFile = Files.createTempFile("firmament-repo", ".zip")
		Files.newOutputStream(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
			.use { outputStream ->
				response.forInputStream().await().use { inputStream ->
					inputStream.copyTo(outputStream)
				}
			}
		targetFile
	}

	/**
	 * Downloads the latest repository from github, setting [latestSavedVersionHash].
	 * @return true, if an update was performed, false, otherwise (no update needed, or wasn't able to complete update)
	 */
	suspend fun downloadUpdate(force: Boolean, branch: String? = null): Boolean =
		withContext(CoroutineName("Repo Update Check")) {
			val latestSha = requestLatestGithubSha(branch)
			if (latestSha == null) {
				logger.warn("Could not request github API to retrieve latest REPO sha.")
				return@withContext false
			}
			val currentSha = loadSavedVersionHash()
			if (latestSha != currentSha || force) {
				val requestUrl =
					"https://github.com/${RepoManager.TConfig.username}/${RepoManager.TConfig.reponame}/archive/$latestSha.zip"
				logger.info("Planning to upgrade repository from $currentSha to $latestSha from $requestUrl")
				val zipFile = downloadGithubArchive(requestUrl)
				logger.info("Download repository zip file to $zipFile. Deleting old repository")
				withContext(IO) { repoSavedLocation.toFile().deleteRecursively() }
				logger.info("Extracting new repository")
				withContext(IO) { extractNewRepository(zipFile) }
				logger.info("Repository loaded on disk.")
				saveVersionHash(latestSha)
				return@withContext true
			} else {
				logger.debug("Repository on latest sha $currentSha. Not performing update")
				return@withContext false
			}
		}

	private fun extractNewRepository(zipFile: Path) {
		repoSavedLocation.createDirectories()
		ZipInputStream(zipFile.inputStream()).use { cis ->
			while (true) {
				val entry = cis.nextEntry ?: break
				if (entry.isDirectory) continue
				val extractedLocation =
					repoSavedLocation.resolve(
						entry.name.substringAfter('/', missingDelimiterValue = "")
					)
				if (repoSavedLocation !in extractedLocation.iterate { it.parent }) {
					logger.error("Notfimament detected an invalid zip file. This is a potential security risk.")
					throw RuntimeException("Notfimament detected an invalid zip file. This is a potential security risk.")
				}
				extractedLocation.parent.createDirectories()
				extractedLocation.outputStream().use { cis.copyTo(it) }
			}
		}
	}


}
