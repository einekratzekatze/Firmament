package moe.nea.notfimament.features.macros

import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import moe.nea.notfimament.keybindings.SavedKeyBinding
import moe.nea.notfimament.util.ErrorUtil

sealed interface KeyComboTrie {
	val label: Component

	companion object {
		fun fromComboList(
			combos: List<ComboKeyAction>,
		): Branch {
			val root = Branch(mutableMapOf())
			for (combo in combos) {
				var p = root
				if (combo.keySequence.isEmpty()) {
					ErrorUtil.softUserError("Key Combo for ${combo.action.label.string} is empty")
					continue
				}
				for ((index, key) in combo.keySequence.withIndex()) {
					val m = (p.nodes as MutableMap)
					if (index == combo.keySequence.lastIndex) {
						if (key in m) {
							ErrorUtil.softUserError("Overlapping actions found for ${combo.keySequence.joinToString(" > ")} (another action ${m[key]} already exists).")
							break
						}

						m[key] = Leaf(combo.action)
					} else {
						val c = m.getOrPut(key) { Branch(mutableMapOf()) }
						if (c !is Branch) {
							ErrorUtil.softUserError("Overlapping actions found for ${combo.keySequence} (final node exists at index $index) through another action already")
							break
						} else {
							p = c
						}
					}
				}
			}
			return root
		}
	}
}

@Serializable
data class MacroWheel(
	val keyBinding: SavedKeyBinding = SavedKeyBinding.unbound(),
	val options: List<HotkeyAction>
)

@Serializable
data class ComboKeyAction(
	val action: HotkeyAction,
	val keySequence: List<SavedKeyBinding> = listOf(),
)

data class Leaf(val action: HotkeyAction) : KeyComboTrie {
	override val label: Component
		get() = action.label

	fun execute() {
		action.execute()
	}
}

data class Branch(
	val nodes: Map<SavedKeyBinding, KeyComboTrie>
) : KeyComboTrie {
	override val label: Component
		get() = Component.literal("...") // TODO: better labels
}
