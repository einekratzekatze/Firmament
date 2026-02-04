package moe.nea.notfimament.apis

import java.util.UUID
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.util.ErrorUtil
import moe.nea.notfimament.util.MinecraftDispatcher
import moe.nea.notfimament.util.net.HttpUtil

object Routes {
	private val nameToUUID: MutableMap<String, Deferred<UUID?>> = mutableMapOf()
	private val UUIDToName: MutableMap<UUID, Deferred<String?>> = mutableMapOf()

	suspend fun getPlayerNameForUUID(uuid: UUID): String? {
		return withContext(MinecraftDispatcher) {
			UUIDToName.computeIfAbsent(uuid) {
				async(Firmament.coroutineScope.coroutineContext) {
					val data = ErrorUtil.catch("could not get name for uuid $uuid") {
						HttpUtil.request("https://mowojang.matdoes.dev/$uuid")
							.forJson<MowojangNameLookup>()
							.await()
					}.orNull() ?: return@async null
					launch(MinecraftDispatcher) {
						nameToUUID[data.name] = async { data.id }
					}
					data.name
				}
			}
		}.await()
	}

	suspend fun getUUIDForPlayerName(name: String): UUID? {
		return withContext(MinecraftDispatcher) {
			nameToUUID.computeIfAbsent(name) {
				async(Firmament.coroutineScope.coroutineContext) {
					val data =
						ErrorUtil.catch("could not get uuid for name $name") {
							HttpUtil.request("https://mowojang.matdoes.dev/$name")
								.forJson<MowojangNameLookup>()
								.await()
						}.orNull() ?: return@async null
					launch(MinecraftDispatcher) {
						UUIDToName[data.id] = async { data.name }
					}
					data.id
				}
			}
		}.await()
	}
}
