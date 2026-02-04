package moe.nea.notfimament.util.mc

import net.minecraft.world.item.component.CustomData
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import moe.nea.notfimament.mixins.accessor.AccessorNbtComponent

fun Iterable<Tag>.toNbtList() = ListTag().also {
	for (element in this) {
		it.add(element)
	}
}

@Suppress("CAST_NEVER_SUCCEEDS")
val CustomData.unsafeNbt get() = (this as AccessorNbtComponent).unsafeNbt_firmament
