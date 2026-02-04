package moe.nea.notfimament.features.texturepack

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.internal.LazilyParsedNumber
import java.util.function.Predicate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import moe.nea.notfimament.util.json.intoGson
import moe.nea.notfimament.util.json.intoKotlinJson
import moe.nea.notfimament.util.removeColorCodes

@Serializable(with = StringMatcher.Serializer::class)
interface StringMatcher {
	fun matches(string: String): Boolean
	fun matches(text: Component): Boolean {
		return matches(text.string)
	}

	val asRegex: java.util.regex.Pattern

	fun matchWithGroups(string: String): MatchNamedGroupCollection?

	fun matches(nbt: StringTag): Boolean {
		val string = nbt.value
		val jsonStart = string.indexOf('{')
		val stringStart = string.indexOf('"')
		val isString = stringStart >= 0 && string.subSequence(0, stringStart).isBlank()
		val isJson = jsonStart >= 0 && string.subSequence(0, jsonStart).isBlank()
		if (isString || isJson) {
			// TODO: return matches(TextCodecs.CODEC.parse(MC.defaultRegistryNbtOps, string) ?: return false)
		}
		return matches(string)
	}

	class Equals(input: String, val stripColorCodes: Boolean) : StringMatcher {
		override val asRegex by lazy(LazyThreadSafetyMode.PUBLICATION) { input.toPattern(java.util.regex.Pattern.LITERAL) }
		private val expected = if (stripColorCodes) input.removeColorCodes() else input
		override fun matches(string: String): Boolean {
			return expected == (if (stripColorCodes) string.removeColorCodes() else string)
		}

		override fun matchWithGroups(string: String): MatchNamedGroupCollection? {
			if (matches(string))
				return object : MatchNamedGroupCollection {
					override fun get(name: String): MatchGroup? {
						return null
					}

					override fun get(index: Int): MatchGroup? {
						return null
					}

					override val size: Int
						get() = 0

					override fun isEmpty(): Boolean {
						return true
					}

					override fun contains(element: MatchGroup?): Boolean {
						return false
					}

					override fun iterator(): Iterator<MatchGroup?> {
						return emptyList<MatchGroup>().iterator()
					}

					override fun containsAll(elements: Collection<MatchGroup?>): Boolean {
						return elements.isEmpty()
					}
				}
			return null
		}

		override fun toString(): String {
			return "Equals($expected, stripColorCodes = $stripColorCodes)"
		}
	}

	class Pattern(val patternWithColorCodes: String, val stripColorCodes: Boolean) : StringMatcher {
		private val pattern = patternWithColorCodes.toRegex()
		override val asRegex = pattern.toPattern()
		override fun matches(string: String): Boolean {
			return pattern.matches(if (stripColorCodes) string.removeColorCodes() else string)
		}

		override fun matchWithGroups(string: String): MatchNamedGroupCollection? {
			return pattern.matchEntire(if (stripColorCodes) string.removeColorCodes() else string)?.groups as MatchNamedGroupCollection?
		}

		override fun toString(): String {
			return "Pattern($patternWithColorCodes, stripColorCodes = $stripColorCodes)"
		}
	}

	object Serializer : KSerializer<StringMatcher> {
		val delegateSerializer = kotlinx.serialization.json.JsonElement.serializer()
		override val descriptor: SerialDescriptor
			get() = SerialDescriptor("StringMatcher", delegateSerializer.descriptor)

		override fun deserialize(decoder: Decoder): StringMatcher {
			val delegate = decoder.decodeSerializableValue(delegateSerializer)
			val gsonDelegate = delegate.intoGson()
			return parse(gsonDelegate)
		}

		override fun serialize(encoder: Encoder, value: StringMatcher) {
			encoder.encodeSerializableValue(delegateSerializer, serialize(value).intoKotlinJson())
		}

	}

	companion object {
		fun serialize(stringMatcher: StringMatcher): JsonElement {
			TODO("Cannot serialize string matchers rn")
		}

		fun parse(jsonElement: JsonElement): StringMatcher {
			if (jsonElement is JsonPrimitive) {
				return Equals(jsonElement.asString, true)
			}
			if (jsonElement is JsonObject) {
				val regex = jsonElement["regex"] as JsonPrimitive?
				val text = jsonElement["equals"] as JsonPrimitive?
				val shouldStripColor = when (val color = (jsonElement["color"] as JsonPrimitive?)?.asString) {
					"preserve" -> false
					"strip", null -> true
					else -> error("Unknown color preservation mode: $color")
				}
				if ((regex == null) == (text == null)) error("Could not parse $jsonElement as string matcher")
				if (regex != null)
					return Pattern(regex.asString, shouldStripColor)
				if (text != null)
					return Equals(text.asString, shouldStripColor)
			}
			error("Could not parse $jsonElement as a string matcher")
		}
	}
}
