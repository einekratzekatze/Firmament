package moe.nea.notfimament.keybindings

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.gui.config.ManagedOption
import moe.nea.notfimament.util.TestUtil
import moe.nea.notfimament.util.data.ManagedConfig

object FirmamentKeyBindings {
	val cats = mutableMapOf<ManagedConfig.Category, KeyMapping.Category>()


	fun registerKeyBinding(name: String, config: ManagedOption<SavedKeyBinding>) {
		val vanillaKeyBinding = KeyMapping(
			name,
			InputConstants.Type.KEYSYM,
			-1,
			cats.computeIfAbsent(config.element.category) {
				KeyMapping.Category.register(Firmament.identifier(it.name.lowercase()))
			}
		)
		if (!TestUtil.isInTest) {
			KeyBindingHelper.registerKeyBinding(vanillaKeyBinding)
		}
		keyBindings[vanillaKeyBinding] = config
	}

	val keyBindings = mutableMapOf<KeyMapping, ManagedOption<SavedKeyBinding>>()

}
