
package moe.nea.notfimament.gui.entity

import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity

object ModifyInvisible : EntityModifier {
    override fun apply(entity: LivingEntity, info: JsonObject): LivingEntity {
        entity.isInvisible = info.get("invisible")?.asBoolean ?: true
        return entity
    }

}
