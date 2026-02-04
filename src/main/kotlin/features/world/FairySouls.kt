package moe.nea.notfimament.features.world

import io.github.moulberry.repo.data.Coordinate
import me.shedaniel.math.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ProcessChatEvent
import moe.nea.notfimament.events.SkyblockServerUpdateEvent
import moe.nea.notfimament.events.WorldRenderLastEvent
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.SBData
import moe.nea.notfimament.util.SkyBlockIsland
import moe.nea.notfimament.util.blockPos
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.data.ProfileSpecificDataHolder
import moe.nea.notfimament.util.render.RenderInWorldContext.Companion.renderInWorld
import moe.nea.notfimament.util.unformattedString


object FairySouls {


	@Serializable
	data class Data(
		val foundSouls: MutableMap<SkyBlockIsland, MutableSet<Int>> = mutableMapOf()
	)

	@Config
	object DConfig : ProfileSpecificDataHolder<Data>(serializer(), "found-fairysouls", ::Data)

	@Config
	object TConfig : ManagedConfig("fairy-souls", Category.MISC) {
		val displaySouls by toggle("show") { false }
		val resetSouls by button("reset") {
			DConfig.data?.foundSouls?.clear() != null
			updateMissingSouls()
		}
	}


	val identifier: String get() = "fairy-souls"

	val playerReach = 5
	val playerReachSquared = playerReach * playerReach

	var currentLocationName: SkyBlockIsland? = null
	var currentLocationSouls: List<Coordinate> = emptyList()
	var currentMissingSouls: List<Coordinate> = emptyList()

	fun updateMissingSouls() {
		currentMissingSouls = emptyList()
		val c = DConfig.data ?: return
		val fi = c.foundSouls[currentLocationName] ?: setOf()
		val cms = currentLocationSouls.toMutableList()
		fi.asSequence().sortedDescending().filter { it in cms.indices }.forEach { cms.removeAt(it) }
		currentMissingSouls = cms
	}

	fun updateWorldSouls() {
		currentLocationSouls = emptyList()
		val loc = currentLocationName ?: return
		currentLocationSouls = RepoManager.neuRepo.constants.fairySouls.soulLocations[loc.locrawMode] ?: return
	}

	fun findNearestClickableSoul(): Coordinate? {
		val player = MC.player ?: return null
		val pos = player.position
		val location = SBData.skyblockLocation ?: return null
		val soulLocations: List<Coordinate> =
			RepoManager.neuRepo.constants.fairySouls.soulLocations[location.locrawMode] ?: return null
		return soulLocations
			.map { it to it.blockPos.distToCenterSqr(pos) }
			.filter { it.second < playerReachSquared }
			.minByOrNull { it.second }
			?.first
	}

	private fun markNearestSoul() {
		val nearestSoul = findNearestClickableSoul() ?: return
		val c = DConfig.data ?: return
		val loc = currentLocationName ?: return
		val idx = currentLocationSouls.indexOf(nearestSoul)
		c.foundSouls.computeIfAbsent(loc) { mutableSetOf() }.add(idx)
		DConfig.markDirty()
		updateMissingSouls()
	}

	@Subscribe
	fun onWorldRender(it: WorldRenderLastEvent) {
		if (!TConfig.displaySouls) return
		renderInWorld(it) {
			currentMissingSouls.forEach {
				block(it.blockPos, Color.ofRGBA(176, 0, 255, 128).color)
			}
			currentLocationSouls.forEach {
				wireframeCube(it.blockPos)
			}
		}
	}

	@Subscribe
	fun onProcessChat(it: ProcessChatEvent) {
		when (it.text.unformattedString) {
			"You have already found that Fairy Soul!" -> {
				markNearestSoul()
			}

			"SOUL! You found a Fairy Soul!" -> {
				markNearestSoul()
			}
		}
	}

	@Subscribe
	fun onLocationChange(it: SkyblockServerUpdateEvent) {
		currentLocationName = it.newLocraw?.skyblockLocation
		updateWorldSouls()
		updateMissingSouls()
	}
}
