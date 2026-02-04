package moe.nea.notfimament.features.debug

import com.mojang.authlib.GameProfile
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Duration.Companion.seconds
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.EntityUpdateEvent
import moe.nea.notfimament.events.IsSlotProtectedEvent
import moe.nea.notfimament.util.ClipboardUtils
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.TimeMark
import moe.nea.notfimament.util.extraAttributes
import moe.nea.notfimament.util.json.toJsonArray
import moe.nea.notfimament.util.math.GChainReconciliation.shortenCycle
import moe.nea.notfimament.util.mc.displayNameAccordingToNbt
import moe.nea.notfimament.util.mc.loreAccordingToNbt
import moe.nea.notfimament.util.rawSkyBlockId
import moe.nea.notfimament.util.toTicks
import moe.nea.notfimament.util.tr


object SkinPreviews {

	// TODO: add pet support
	@Subscribe
	fun onEntityUpdate(event: EntityUpdateEvent) {
		if (!isRecording) return
		if (event.entity.position != pos)
			return
		val entity = event.entity as? LivingEntity ?: return
		val stack = entity.getItemBySlot(EquipmentSlot.HEAD) ?: return
		val profile = stack.get(DataComponents.PROFILE)?.partialProfile() ?: return
		if (profile == animation.lastOrNull()) return
		animation.add(profile)
		val shortened = animation.shortenCycle()
		if (shortened.size <= (animation.size / 2).coerceAtLeast(1) && lastDiscard.passedTime() > 2.seconds) {
			val tickEstimation = (lastDiscard.passedTime() / animation.size).toTicks()
			val skinName = if (skinColor != null) "${skinId}_${skinColor?.replace(" ", "_")?.uppercase()}" else skinId!!
			val json =
				buildJsonObject {
					put("ticks", tickEstimation)
					put(
						"textures",
						shortened.map {
							it.id.toString() + ":" + it.properties()["textures"].first().value()
						}.toJsonArray()
					)
				}
			MC.sendChat(
				tr(
					"firmament.dev.skinpreviews.done",
					"Observed a total of ${animation.size} elements, which could be shortened to a cycle of ${shortened.size}. Copying JSON array. Estimated ticks per frame: $tickEstimation."
				)
			)
			isRecording = false
			ClipboardUtils.setTextContent(JsonPrimitive(skinName).toString() + ":" + json.toString())
		}
	}

	var animation = mutableListOf<GameProfile>()
	var pos = Vec3(-1.0, 72.0, -101.25)
	var isRecording = false
	var skinColor: String? = null
	var skinId: String? = null
	var lastDiscard = TimeMark.farPast()

	@Subscribe
	fun onActivate(event: IsSlotProtectedEvent) {
		if (!PowerUserTools.TConfig.autoCopyAnimatedSkins) return
		val lastLine = event.itemStack.loreAccordingToNbt.lastOrNull()?.string
		if (lastLine != "Right-click to preview!" && lastLine != "Click to preview!") return
		lastDiscard = TimeMark.now()
		val stackName = event.itemStack.displayNameAccordingToNbt.string
		if (stackName == "FIRE SALE!") {
			skinColor = null
			skinId = event.itemStack.rawSkyBlockId
		} else {
			skinColor = stackName
		}
		animation.clear()
		isRecording = true
		MC.sendChat(tr("firmament.dev.skinpreviews.start", "Starting to observe items"))
	}
}
