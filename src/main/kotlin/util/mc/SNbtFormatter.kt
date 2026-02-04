package moe.nea.notfimament.util.mc

import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.TagVisitor

class SNbtFormatter private constructor() : TagVisitor {
	private val result = StringBuilder()
	private var indent = 0
	private fun writeIndent() {
		result.append("\t".repeat(indent))
	}

	private fun pushIndent() {
		indent++
	}

	private fun popIndent() {
		indent--
	}

	fun apply(element: Tag): StringBuilder {
		element.accept(this)
		return result
	}


	override fun visitString(element: StringTag) {
		result.append(StringTag.quoteAndEscape(element.value))
	}

	override fun visitByte(element: ByteTag) {
		result.append(element.box()).append("b")
	}

	override fun visitShort(element: ShortTag) {
		result.append(element.shortValue()).append("s")
	}

	override fun visitInt(element: IntTag) {
		result.append(element.intValue())
	}

	override fun visitLong(element: LongTag) {
		result.append(element.longValue()).append("L")
	}

	override fun visitFloat(element: FloatTag) {
		result.append(element.floatValue()).append("f")
	}

	override fun visitDouble(element: DoubleTag) {
		result.append(element.doubleValue()).append("d")
	}

	private fun visitArrayContents(array: CollectionTag) {
		array.forEachIndexed { index, element ->
			writeIndent()
			element.accept(this)
			if (array.size() != index + 1) {
				result.append(",")
			}
			result.append("\n")
		}
	}

	private fun writeArray(arrayTypeTag: String, array: CollectionTag) {
		result.append("[").append(arrayTypeTag).append("\n")
		pushIndent()
		visitArrayContents(array)
		popIndent()
		writeIndent()
		result.append("]")

	}

	override fun visitByteArray(element: ByteArrayTag) {
		writeArray("B;", element)
	}

	override fun visitIntArray(element: IntArrayTag) {
		writeArray("I;", element)
	}

	override fun visitLongArray(element: LongArrayTag) {
		writeArray("L;", element)
	}

	override fun visitList(element: ListTag) {
		writeArray("", element)
	}

	override fun visitCompound(compound: CompoundTag) {
		result.append("{\n")
		pushIndent()
		val keys = compound.keySet().sorted()
		keys.forEachIndexed { index, key ->
			writeIndent()
			val element = compound[key] ?: error("Key '$key' found but not present in compound: $compound")
			val escapedName = escapeName(key)
			result.append(escapedName).append(": ")
			element.accept(this)
			if (keys.size != index + 1) {
				result.append(",")
			}
			result.append("\n")
		}
		popIndent()
		writeIndent()
		result.append("}")
	}

	override fun visitEnd(element: EndTag) {
		result.append("END")
	}

	companion object {
		fun prettify(nbt: Tag): String {
			return SNbtFormatter().apply(nbt).toString()
		}

		fun Tag.toPrettyString() = prettify(this)

		fun escapeName(key: String): String =
			if (key.matches(SIMPLE_NAME)) key else StringTag.quoteAndEscape(key)

		val SIMPLE_NAME = "[A-Za-z0-9._+-]+".toRegex()
	}
}
