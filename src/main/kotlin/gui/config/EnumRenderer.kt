package moe.nea.notfimament.gui.config

import net.minecraft.network.chat.Component

interface EnumRenderer<E : Any> {
	fun getName(option: ManagedOption<E>, value: E): Component

	companion object {
		fun <E : Enum<E>> default() = object : EnumRenderer<E> {
			override fun getName(option: ManagedOption<E>, value: E): Component {
				return Component.translatable(option.rawLabelText + ".choice." + value.name.lowercase())
			}
		}
	}
}
