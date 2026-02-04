package moe.nea.notfimament.features.texturepack

import java.util.regex.Matcher
import util.json.CodecSerializer
import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import moe.nea.notfimament.util.directLiteralStringContent
import moe.nea.notfimament.util.transformEachRecursively

@Serializable
data class TreeishTextReplacer(
	val match: StringMatcher,
	val replacements: List<SubPartReplacement>
) {
	@Serializable
	data class SubPartReplacement(
        val match: StringMatcher,
        val style: @Serializable(StyleSerializer::class) Style? = null,
        val replace: @Serializable(TextSerializer::class) Component,
	)

	object TextSerializer : CodecSerializer<Component>(ComponentSerialization.CODEC)
	object StyleSerializer : CodecSerializer<Style>(Style.Serializer.CODEC)
	companion object {
		val pattern = "[$]\\{(?<name>[^}]+)}".toPattern()
		fun injectMatchResults(text: Component, matches: Matcher): Component {
			return text.transformEachRecursively { it ->
				val content = it.directLiteralStringContent ?: return@transformEachRecursively it
				val matcher = pattern.matcher(content)
				val builder = StringBuilder()
				while (matcher.find()) {
					matcher.appendReplacement(builder, matches.group(matcher.group("name")).toString())
				}
				matcher.appendTail(builder)
				Component.literal(builder.toString()).setStyle(it.style)
			}
		}
	}

	fun match(text: Component): Boolean {
		return match.matches(text)
	}

	fun replaceText(text: Component): Component {
		return text.transformEachRecursively { part ->
			var part: Component = part
			for (replacement in replacements) {
				val rawPartText = part.string
				replacement.style?.let { expectedStyle ->
					val parentStyle = part.style
					val parented = expectedStyle.applyTo(parentStyle)
					if (parented.isStrikethrough != parentStyle.isStrikethrough
						|| parented.isObfuscated != parentStyle.isObfuscated
						|| parented.isBold != parentStyle.isBold
						|| parented.isUnderlined != parentStyle.isUnderlined
						|| parented.isItalic != parentStyle.isItalic
						|| parented.color?.value != parentStyle.color?.value)
						continue
				}
				val matcher = replacement.match.asRegex.matcher(rawPartText)
				if (!matcher.find()) continue
				val p = Component.literal("")
				p.setStyle(part.style)
				var lastAppendPosition = 0
				do {
					p.append(rawPartText.substring(lastAppendPosition, matcher.start()))
					lastAppendPosition = matcher.end()
					p.append(injectMatchResults(replacement.replace, matcher))
				} while (matcher.find())
				p.append(rawPartText.substring(lastAppendPosition))
				part = p
			}
			part
		}
	}

}
