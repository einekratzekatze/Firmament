package moe.nea.notfimament.features.texturepack.predicates

import com.google.gson.JsonElement
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicate
import moe.nea.notfimament.features.texturepack.FirmamentModelPredicateParser
import moe.nea.notfimament.features.texturepack.StringMatcher
import moe.nea.notfimament.util.mc.decodeProfileTextureProperty
import moe.nea.notfimament.util.parsePotentiallyDashlessUUID

class SkullPredicate(
	val profileId: UUID?,
	val textureProfileId: UUID?,
	val skinUrl: StringMatcher?,
	val textureValue: StringMatcher?,
) : FirmamentModelPredicate {
	object Parser : FirmamentModelPredicateParser {
		override fun parse(jsonElement: JsonElement): FirmamentModelPredicate? {
			val obj = jsonElement.asJsonObject
			val profileId = obj.getAsJsonPrimitive("profileId")
				?.asString?.let(::parsePotentiallyDashlessUUID)
			val textureProfileId = obj.getAsJsonPrimitive("textureProfileId")
				?.asString?.let(::parsePotentiallyDashlessUUID)
			val textureValue = obj.get("textureValue")?.let(StringMatcher::parse)
			val skinUrl = obj.get("skinUrl")?.let(StringMatcher::parse)
			return SkullPredicate(profileId, textureProfileId, skinUrl, textureValue)
		}
	}

	override fun test(stack: ItemStack, holder: LivingEntity?): Boolean {
		if (!stack.`is`(Items.PLAYER_HEAD)) return false
		val profile = stack.get(DataComponents.PROFILE) ?: return false
		val textureProperty = profile.partialProfile().properties["textures"].firstOrNull()
		val textureMode = lazy(LazyThreadSafetyMode.NONE) {
			decodeProfileTextureProperty(textureProperty ?: return@lazy null)
		}
		when {
			profileId != null
				&& profileId != profile.partialProfile().id ->
				return false

			textureValue != null
				&& !textureValue.matches(textureProperty?.value ?: "") ->
				return false

			skinUrl != null
				&& !skinUrl.matches(textureMode.value?.textures?.get(MinecraftProfileTexture.Type.SKIN)?.url ?: "") ->
				return false

			textureProfileId != null
				&& textureProfileId != textureMode.value?.profileId ->
				return false

			else -> return true
		}
	}
}
