
package moe.nea.notfimament.gui.entity

import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.Creeper

object ModifyCharged : EntityModifier {
    override fun apply(entity: LivingEntity, info: JsonObject): LivingEntity {
        require(entity is Creeper)
        entity.entityData.set(Creeper.DATA_IS_POWERED, true)
        return entity
    }
}
