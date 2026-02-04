package moe.nea.notfimament.util.data

import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.gui.config.storage.ConfigStorageClass
import moe.nea.notfimament.gui.config.storage.FirmamentConfigLoader
import moe.nea.notfimament.util.SBData

sealed class IDataHolder<T> {
	fun markDirty(future: CompletableFuture<Void?>? = null) {
		FirmamentConfigLoader.markDirty(this, future)
	}

	init {
		require(this.javaClass.getAnnotation(Config::class.java) != null)
	}

	abstract fun keys(): Collection<T>
	abstract fun saveTo(key: T): JsonObject
	abstract fun loadFrom(key: T, jsonObject: JsonObject)
	abstract fun explicitDefaultLoad()
	abstract fun clear()
	abstract val storageClass: ConfigStorageClass
}

open class ProfileKeyedConfig<T>(
	val prefix: String,
	val serializer: KSerializer<T>,
	val default: () -> T & Any,
) : IDataHolder<UUID>() {

	override val storageClass: ConfigStorageClass
		get() = ConfigStorageClass.PROFILE
	private var _data: MutableMap<UUID, T>? = null

	val data: T & Any
		get() {
			val map = _data ?: error("Config $this not loaded — forgot to register?")
			map[SBData.profileIdOrNil]?.let { return it }
			val newValue = default()
			map[SBData.profileIdOrNil] = newValue
			return newValue
		}

	override fun keys(): Collection<UUID> {
		return _data!!.keys
	}

	override fun saveTo(key: UUID): JsonObject {
		val d = _data!!
		return buildJsonObject {
			put(prefix, Firmament.json.encodeToJsonElement(serializer, d[key] ?: return@buildJsonObject))
		}
	}

	override fun loadFrom(key: UUID, jsonObject: JsonObject) {
		var map = _data
		if (map == null) {
			map = mutableMapOf()
			_data = map
		}
		map[key] =
			jsonObject[prefix]
				?.let {
					Firmament.json.decodeFromJsonElement(serializer, it)
				} ?: default()
	}

	override fun explicitDefaultLoad() {
		_data = mutableMapOf()
	}

	override fun clear() {
		_data = null
	}
}

abstract class GenericConfig<T>(
	val prefix: String,
	val serializer: KSerializer<T>,
	val default: () -> T,
) : IDataHolder<Unit>() {

	private var _data: T? = null

	val data get() = _data ?: error("Config $this not loaded — forgot to register?")

	override fun keys(): Collection<Unit> {
		return listOf(Unit)
	}

	override fun explicitDefaultLoad() {
		_data = default()
	}

	open fun onLoad() {
	}

	override fun saveTo(key: Unit): JsonObject {
		return buildJsonObject {
			put(prefix, Firmament.json.encodeToJsonElement(serializer, data))
		}
	}

	override fun loadFrom(key: Unit, jsonObject: JsonObject) {
		_data = jsonObject[prefix]?.let { Firmament.json.decodeFromJsonElement(serializer, it) } ?: default()
		onLoad()
	}

	override fun clear() {
		_data = null
	}
}
