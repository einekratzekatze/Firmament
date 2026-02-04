package moe.nea.notfimament.apis

import java.net.URI
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant
import java.util.OptionalLong
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlin.jvm.optionals.getOrNull
import net.minecraft.client.Minecraft
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.util.net.HttpUtil

object UrsaManager {
	private data class Token(
		val validUntil: Instant,
		val token: String,
		val obtainedFrom: String,
	) {
		fun isValid(host: String) = Instant.now().plusSeconds(60) < validUntil && obtainedFrom == host
	}

	private var currentToken: Token? = null
	private val lock = Mutex()
	private fun getToken(host: String) = currentToken?.takeIf { it.isValid(host) }

	suspend fun <T> request(path: List<String>, bodyHandler: HttpResponse.BodyHandler<T>): T {
		var didLock = false
		try {
			val host = "ursa.notenoughupdates.org"
			var token = getToken(host)
			if (token == null) {
				lock.lock()
				didLock = true
				token = getToken(host)
			}
			var url = URI.create("https://$host")
			for (segment in path) {
				url = url.resolve(segment)
			}
			val request = HttpUtil.request(url)
			if (token == null) {
				withContext(Dispatchers.IO) {
					val mc = Minecraft.getInstance()
					val serverId = UUID.randomUUID().toString()
					mc.services().sessionService.joinServer(mc.user.profileId, mc.user.accessToken, serverId)
					request.header("x-ursa-username", mc.user.name)
					request.header("x-ursa-serverid", serverId)
				}
			} else {
				request.header("x-ursa-token", token.token)
			}
			val response = request.execute(bodyHandler)
				.await()
			val savedToken = response.headers().firstValue("x-ursa-token").getOrNull()
			if (savedToken != null) {
				val validUntil = response.headers().firstValueAsLong("x-ursa-expires").orNull()?.let { Instant.ofEpochMilli(it) }
					?: (Instant.now() + Duration.ofMinutes(55))
				currentToken = Token(validUntil, savedToken, host)
			}
			if (response.statusCode() != 200) {
				Firmament.logger.error("Failed to contact ursa minor: ${response.statusCode()}")
			}
			return response.body()
		} finally {
			if (didLock)
				lock.unlock()
		}
	}
}

private fun OptionalLong.orNull(): Long? {
	if (this.isPresent)return null
	return this.asLong
}
