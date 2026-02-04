
package moe.nea.notfimament.events

import net.minecraft.world.entity.Entity

data class EntityDespawnEvent(
    val entity: Entity?, val entityId: Int,
    val reason: Entity.RemovalReason,
) : FirmamentEvent() {
    companion object: FirmamentEventBus<EntityDespawnEvent>()
}
