
package moe.nea.notfimament.gui.entity

import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity

object ModifyRiding : EntityModifier {
    override fun apply(entity: LivingEntity, info: JsonObject): LivingEntity {
        val newEntity = EntityRenderer.constructEntity(info)
        require(newEntity != null)
        newEntity.startRiding(entity, true, false)
        return entity
    }

}
