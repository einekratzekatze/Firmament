package moe.nea.notfimament.util.skyblock

import org.intellij.lang.annotations.Language
import net.minecraft.network.chat.Component
import moe.nea.notfimament.util.StringUtil.title
import moe.nea.notfimament.util.StringUtil.unwords
import moe.nea.notfimament.util.mc.MCTabListAPI
import moe.nea.notfimament.util.unformattedString

object TabListAPI {

	fun getWidgetLines(widgetName: WidgetName, includeTitle: Boolean = false, from: MCTabListAPI.CurrentTabList = MCTabListAPI.currentTabList): List<Component> {
		return from.body
			.dropWhile { !widgetName.matchesTitle(it) }
			.takeWhile { it.string.isNotBlank() && !it.string.startsWith("               ") }
			.let { if (includeTitle) it else it.drop(1) }
	}

	enum class WidgetName(regex: Regex?) {
		COMMISSIONS,
		SKILLS("Skills:( .*)?"),
		PROFILE("Profile: (.*)"),
		COLLECTION,
		ESSENCE,
		PET
		;

		fun matchesTitle(it: Component): Boolean {
			return regex.matches(it.unformattedString)
		}

		constructor() : this(null)
		constructor(@Language("RegExp") regex: String) : this(Regex(regex))

		val label =
			name.split("_").map { it.lowercase().title() }.unwords()
		val regex = regex ?: Regex.fromLiteral("$label:")

	}

}
