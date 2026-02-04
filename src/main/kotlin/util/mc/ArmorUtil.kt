package moe.nea.notfimament.util.mc

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity

val LivingEntity.iterableArmorItems
	get() = EquipmentSlot.entries.asSequence()
		.map { it to getItemBySlot(it) }
