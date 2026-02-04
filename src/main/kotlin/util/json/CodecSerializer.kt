package util.json

import com.mojang.serialization.Codec
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import moe.nea.notfimament.util.json.KJsonOps

abstract class CodecSerializer<T>(val codec: Codec<T>) : KSerializer<T> {
	override val descriptor: SerialDescriptor
		get() = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeSerializableValue(
			JsonElement.serializer(),
			codec.encodeStart(KJsonOps.INSTANCE, value).orThrow
		)
	}

	override fun deserialize(decoder: Decoder): T {
		return codec.decode(KJsonOps.INSTANCE, decoder.decodeSerializableValue(JsonElement.serializer()))
			.orThrow.first
	}
}
