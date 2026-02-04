package moe.nea.notfimament.util

import java.util.Base64

object Base64Util {
	fun decodeString(str: String): String {
		return decodeBytes(str).decodeToString()
	}

	fun decodeBytes(str: String): ByteArray {
		return Base64.getDecoder().decode(str.padToValidBase64())
	}

	fun String.padToValidBase64(): String {
		val align = this.length % 4
		if (align == 0) return this
		return this + "=".repeat(4 - align)
	}

	fun encodeToString(bytes: ByteArray): String {
		return Base64.getEncoder().encodeToString(bytes)
	}
}
