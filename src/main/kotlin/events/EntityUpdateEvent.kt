package moe.nea.notfimament.events

import com.mojang.datafixers.util.Pair
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.item.ItemStack
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.util.MC

/**
 * This event is fired when some entity properties are updated.
 * It is not fired for common changes like position, but is for less common ones,
 * like health, tracked data, names, equipment. It is always fired
 * *after* the values have been applied to the entity.
 */
sealed class EntityUpdateEvent : FirmamentEvent() {
	companion object : FirmamentEventBus<EntityUpdateEvent>() {
		@Subscribe
		fun onPlayerInventoryUpdate(event: PlayerInventoryUpdate) {
			val p = MC.player ?: return
			val updatedSlots = listOf(
				EquipmentSlot.HEAD to 39,
				EquipmentSlot.CHEST to 38,
				EquipmentSlot.LEGS to 37,
				EquipmentSlot.FEET to 36,
				EquipmentSlot.OFFHAND to 40,
				EquipmentSlot.MAINHAND to p.inventory.selectedSlot, // TODO: also equipment update when you swap your selected slot perhaps
			).mapNotNull { (slot, stackIndex) ->
				val slotIndex = p.inventoryMenu.findSlot(p.inventory, stackIndex).asInt
				event.getOrNull(slotIndex)?.let {
					Pair.of(slot, it)
				}
			}
			if (updatedSlots.isNotEmpty())
				publish(EquipmentUpdate(p, updatedSlots))
		}
	}

	abstract val entity: Entity

	data class AttributeUpdate(
        override val entity: LivingEntity,
        val attributes: List<ClientboundUpdateAttributesPacket.AttributeSnapshot>,
	) : EntityUpdateEvent()

	data class TrackedDataUpdate(
        override val entity: Entity,
        val trackedValues: List<SynchedEntityData.DataValue<*>>,
	) : EntityUpdateEvent()

	data class EquipmentUpdate(
        override val entity: Entity,
        val newEquipment: List<Pair<EquipmentSlot, ItemStack>>,
	) : EntityUpdateEvent()

// TODO: onEntityPassengersSet, onEntityAttach?, onEntityStatusEffect
}
