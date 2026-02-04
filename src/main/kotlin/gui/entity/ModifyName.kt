
package moe.nea.notfimament.gui.entity

import com.google.gson.JsonObject
import net.minecraft.world.entity.LivingEntity
import net.minecraft.network.chat.Component

object ModifyName : EntityModifier {
    override fun apply(entity: LivingEntity, info: JsonObject): LivingEntity {
        entity.customName = Component.literal(info.get("name").asString)
        return entity
    }

}
