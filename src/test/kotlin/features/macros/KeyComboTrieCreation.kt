package moe.nea.notfimament.test.features.macros

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import com.mojang.blaze3d.platform.InputConstants
import moe.nea.notfimament.features.macros.Branch
import moe.nea.notfimament.features.macros.ComboKeyAction
import moe.nea.notfimament.features.macros.CommandAction
import moe.nea.notfimament.features.macros.KeyComboTrie
import moe.nea.notfimament.features.macros.Leaf
import moe.nea.notfimament.keybindings.SavedKeyBinding

class KeyComboTrieCreation {
	val basicAction = CommandAction("ac Hello")
	val aPress = SavedKeyBinding.keyWithoutMods(InputConstants.KEY_A)
	val bPress = SavedKeyBinding.keyWithoutMods(InputConstants.KEY_B)
	val cPress = SavedKeyBinding.keyWithoutMods(InputConstants.KEY_C)

	@Test
	fun testValidShortTrie() {
		val actions = listOf(
			ComboKeyAction(basicAction, listOf(aPress)),
			ComboKeyAction(basicAction, listOf(bPress)),
			ComboKeyAction(basicAction, listOf(cPress)),
		)
		Assertions.assertEquals(
			Branch(
				mapOf(
					aPress to Leaf(basicAction),
					bPress to Leaf(basicAction),
					cPress to Leaf(basicAction),
				),
			), KeyComboTrie.fromComboList(actions)
		)
	}

	@Test
	fun testOverlappingLeafs() {
		Assertions.assertThrows(IllegalStateException::class.java) {
			KeyComboTrie.fromComboList(
				listOf(
					ComboKeyAction(basicAction, listOf(aPress, aPress)),
					ComboKeyAction(basicAction, listOf(aPress, aPress)),
				)
			)
		}
		Assertions.assertThrows(IllegalStateException::class.java) {
			KeyComboTrie.fromComboList(
				listOf(
					ComboKeyAction(basicAction, listOf(aPress)),
					ComboKeyAction(basicAction, listOf(aPress)),
				)
			)
		}
	}

	@Test
	fun testBranchOverlappingLeaf() {
		Assertions.assertThrows(IllegalStateException::class.java) {
			KeyComboTrie.fromComboList(
				listOf(
					ComboKeyAction(basicAction, listOf(aPress)),
					ComboKeyAction(basicAction, listOf(aPress, aPress)),
				)
			)
		}
	}
	@Test
	fun testLeafOverlappingBranch() {
		Assertions.assertThrows(IllegalStateException::class.java) {
			KeyComboTrie.fromComboList(
				listOf(
					ComboKeyAction(basicAction, listOf(aPress, aPress)),
					ComboKeyAction(basicAction, listOf(aPress)),
				)
			)
		}
	}


	@Test
	fun testValidNestedTrie() {
		val actions = listOf(
			ComboKeyAction(basicAction, listOf(aPress, aPress)),
			ComboKeyAction(basicAction, listOf(aPress, bPress)),
			ComboKeyAction(basicAction, listOf(cPress)),
		)
		Assertions.assertEquals(
			Branch(
				mapOf(
					aPress to Branch(
						mapOf(
							aPress to Leaf(basicAction),
							bPress to Leaf(basicAction),
						)
					),
					cPress to Leaf(basicAction),
				),
			), KeyComboTrie.fromComboList(actions)
		)
	}

}
