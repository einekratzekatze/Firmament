package moe.nea.notfimament.util.mc

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component

var ItemStack.loreAccordingToNbt: List<Component>
	get() = get(DataComponents.LORE)?.lines ?: listOf()
    set(value) {
        set(DataComponents.LORE, ItemLore(value))
    }

var ItemStack.displayNameAccordingToNbt: Component
    get() = get(DataComponents.CUSTOM_NAME) ?: get(DataComponents.ITEM_NAME) ?: item.name
    set(value) {
        set(DataComponents.CUSTOM_NAME, value)
    }

fun ItemStack.setCustomName(text: Component) {
    set(DataComponents.CUSTOM_NAME, text)
}
