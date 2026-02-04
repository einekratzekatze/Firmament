
package moe.nea.notfimament.gui.entity

import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.boss.wither.WitherBoss

object ModifyWither : EntityModifier {
    override fun apply(entity: LivingEntity, info: JsonObject): LivingEntity {
        require(entity is WitherBoss)
        info["tiny"]?.let {
            entity.invulnerableTicks = if (it.asBoolean) 800 else 0
        }
        info["armored"]?.let {
            entity.health = if (it.asBoolean) 1F else entity.maxHealth
        }
        return entity
    }

}
