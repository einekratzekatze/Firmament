package moe.nea.notfimament.util.net

import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import moe.nea.notfimament.Firmament

object HttpUtil {
	val httpClient = HttpClient.newBuilder()
		.followRedirects(HttpClient.Redirect.NORMAL)
		.build()

	data class Request(val request: HttpRequest.Builder) {
		fun <T> execute(bodyHandler: HttpResponse.BodyHandler<T>): CompletableFuture<HttpResponse<T>> {
			return httpClient.sendAsync(request.build(), bodyHandler)
		}

		fun <T> forBody(bodyHandler: HttpResponse.BodyHandler<T>): CompletableFuture<T> {
			return execute(bodyHandler).thenApply { it.body() }
		}

		fun forInputStream(): CompletableFuture<InputStream> {
			return forBody(HttpResponse.BodyHandlers.ofInputStream())
		}

		inline fun <reified T> forJson(): CompletableFuture<T> {
			return forJson(serializer())
		}

		fun <T> forJson(serializer: DeserializationStrategy<T>): CompletableFuture<T> {
			return forBody(jsonBodyHandler(serializer))
		}

		fun header(key: String, value: String) {
			request.header(key, value)
		}
	}

	fun <T> jsonBodyHandler(serializer: DeserializationStrategy<T>): HttpResponse.BodyHandler<T> {
		val inp = HttpResponse.BodyHandlers.ofInputStream()
		return HttpResponse.BodyHandler {
			val subscriber = inp.apply(it)
			object : HttpResponse.BodySubscriber<T> {
				override fun getBody(): CompletionStage<T> {
					return subscriber.body.thenApply { Firmament.json.decodeFromStream(serializer, it) }
				}

				override fun onSubscribe(subscription: Flow.Subscription?) {
					subscriber.onSubscribe(subscription)
				}

				override fun onNext(item: List<ByteBuffer?>?) {
					subscriber.onNext(item)
				}

				override fun onError(throwable: Throwable?) {
					subscriber.onError(throwable)
				}

				override fun onComplete() {
					subscriber.onComplete()
				}
			}
		}
	}

	fun request(url: String): Request = request(URI.create(url))
	fun request(url: URL): Request = request(url.toURI())
	fun request(url: URI): Request {
		return Request(
			HttpRequest.newBuilder(url)
				.GET()
				.header("user-agent", "Notfimament/${Firmament.version}")
		)
	}
}
