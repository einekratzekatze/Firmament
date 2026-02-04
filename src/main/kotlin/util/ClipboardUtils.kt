

package moe.nea.notfimament.util

import moe.nea.notfimament.Firmament

object ClipboardUtils {
    fun setTextContent(string: String) {
        try {
            MC.keyboard.clipboard = string.ifEmpty { " " }
        } catch (e: Exception) {
            Firmament.logger.error("Could not write clipboard", e)
        }
    }

    fun getTextContents(): String {
        try {
            return MC.keyboard.clipboard ?: ""
        } catch (e: Exception) {
            Firmament.logger.error("Could not read clipboard", e)
            return ""
        }
    }
}
