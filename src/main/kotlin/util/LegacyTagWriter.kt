package moe.nea.notfimament.util

import kotlinx.serialization.json.JsonPrimitive
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import moe.nea.notfimament.util.mc.SNbtFormatter.Companion.SIMPLE_NAME

class LegacyTagWriter(val compact: Boolean) {
	companion object {
		fun stringify(nbt: Tag, compact: Boolean): String {
			return LegacyTagWriter(compact).also { it.writeElement(nbt) }
				.stringWriter.toString()
		}

		fun Tag.toLegacyString(pretty: Boolean = false): String {
			return stringify(this, !pretty)
		}
	}

	val stringWriter = StringBuilder()
	var indent = 0
	fun newLine() {
		if (compact) return
		stringWriter.append('\n')
		repeat(indent) {
			stringWriter.append("  ")
		}
	}

	fun writeElement(nbt: Tag) {
		when (nbt) {
			is IntTag -> stringWriter.append(nbt.value.toString())
			is StringTag -> stringWriter.append(escapeString(nbt.value))
			is FloatTag -> stringWriter.append(nbt.value).append('F')
			is DoubleTag -> stringWriter.append(nbt.value).append('D')
			is ByteTag -> stringWriter.append(nbt.value).append('B')
			is LongTag -> stringWriter.append(nbt.value).append('L')
			is ShortTag -> stringWriter.append(nbt.value).append('S')
			is CompoundTag -> writeCompound(nbt)
			is EndTag -> {}
			is CollectionTag -> writeArray(nbt)
		}
	}

	fun writeArray(nbt: CollectionTag) {
		stringWriter.append('[')
		indent++
		newLine()
		nbt.forEachIndexed { index, element ->
			writeName(index.toString())
			writeElement(element)
			if (index != nbt.size() - 1) {
				stringWriter.append(',')
				newLine()
			}
		}
		indent--
		if (nbt.size() != 0)
			newLine()
		stringWriter.append(']')
	}

	fun writeCompound(nbt: CompoundTag) {
		stringWriter.append('{')
		indent++
		newLine()
		val entries = nbt.entrySet().sortedBy { it.key }
		entries.forEachIndexed { index, it ->
			writeName(it.key)
			writeElement(it.value)
			if (index != entries.lastIndex) {
				stringWriter.append(',')
				newLine()
			}
		}
		indent--
		if (nbt.size() != 0)
			newLine()
		stringWriter.append('}')
	}

	fun escapeString(string: String): String {
		return JsonPrimitive(string).toString()
	}

	fun escapeName(key: String): String =
		if (key.matches(SIMPLE_NAME)) key else escapeString(key)

	fun writeName(key: String) {
		stringWriter.append(escapeName(key))
		stringWriter.append(':')
		if (!compact) stringWriter.append(' ')
	}
}
