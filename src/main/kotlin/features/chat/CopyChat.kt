package moe.nea.notfimament.features.chat

import net.minecraft.util.FormattedCharSequence
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.reconstitute


object CopyChat {
	val identifier: String
		get() = "copy-chat"

	@Config
	object TConfig : ManagedConfig(identifier, Category.CHAT) {
		val copyChat by toggle("copy-chat") { false }
	}

	fun orderedTextToString(orderedText: FormattedCharSequence): String {
		return orderedText.reconstitute().string
	}
}
