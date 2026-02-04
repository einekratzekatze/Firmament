package moe.nea.notfimament.util.mc

import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import moe.nea.notfimament.util.MC

fun ItemStack.appendLore(args: List<Component>) {
	if (args.isEmpty()) return
	modifyLore {
		val loreList = loreAccordingToNbt.toMutableList()
		for (arg in args) {
			loreList.add(arg)
		}
		loreList
	}
}

fun ItemStack.modifyLore(update: (List<Component>) -> List<Component>) {
	val loreList = loreAccordingToNbt
	loreAccordingToNbt = update(loreList)
}

fun loadItemFromNbt(nbt: CompoundTag, registries: HolderLookup.Provider = MC.defaultRegistries): ItemStack? {
	return ItemStack.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE, registries), nbt).result().getOrNull()?.first
}
