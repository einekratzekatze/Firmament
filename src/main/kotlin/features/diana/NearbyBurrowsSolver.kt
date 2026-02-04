package moe.nea.notfimament.features.diana

import me.shedaniel.math.Color
import kotlin.time.Duration.Companion.seconds
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.core.Position
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ParticleSpawnEvent
import moe.nea.notfimament.events.ProcessChatEvent
import moe.nea.notfimament.events.WorldReadyEvent
import moe.nea.notfimament.events.WorldRenderLastEvent
import moe.nea.notfimament.util.TimeMark
import moe.nea.notfimament.util.collections.mutableMapWithMaxSize
import moe.nea.notfimament.util.render.RenderInWorldContext.Companion.renderInWorld

object NearbyBurrowsSolver {


	private val recentlyDugBurrows: MutableMap<BlockPos, TimeMark> = mutableMapWithMaxSize(20)
	private val recentEnchantParticles: MutableMap<BlockPos, TimeMark> = mutableMapWithMaxSize(500)
	private var lastBlockClick: BlockPos? = null

	enum class BurrowType {
		START, MOB, TREASURE
	}

	val burrows = mutableMapOf<BlockPos, BurrowType>()

	@Subscribe
	fun onChatEvent(event: ProcessChatEvent) {
		val lastClickedBurrow = lastBlockClick ?: return
		if (event.unformattedString.startsWith("You dug out a Griffin Burrow!") ||
			event.unformattedString.startsWith(" ☠ You were killed by") ||
			event.unformattedString.startsWith("You finished the Griffin burrow chain!")
		) {
			markAsDug(lastClickedBurrow)
			burrows.remove(lastClickedBurrow)
		}
	}


	fun wasRecentlyDug(blockPos: BlockPos): Boolean {
		val lastDigTime = recentlyDugBurrows[blockPos] ?: TimeMark.farPast()
		return lastDigTime.passedTime() < 10.seconds
	}

	fun markAsDug(blockPos: BlockPos) {
		recentlyDugBurrows[blockPos] = TimeMark.now()
	}

	fun wasRecentlyEnchanted(blockPos: BlockPos): Boolean {
		val lastEnchantTime = recentEnchantParticles[blockPos] ?: TimeMark.farPast()
		return lastEnchantTime.passedTime() < 4.seconds
	}

	fun markAsEnchanted(blockPos: BlockPos) {
		recentEnchantParticles[blockPos] = TimeMark.now()
	}

	@Subscribe
	fun onParticles(event: ParticleSpawnEvent) {
		if (!DianaWaypoints.TConfig.nearbyWaypoints) return

		val position: BlockPos = event.position.toBlockPos().below()

		if (wasRecentlyDug(position)) return

		val isEven50Spread = (event.offset.x == 0.5f && event.offset.z == 0.5f)

		if (event.particleEffect.type == ParticleTypes.ENCHANT) {
			if (event.count == 5 && event.speed == 0.05F && event.offset.y == 0.4F && isEven50Spread) {
				markAsEnchanted(position)
			}
			return
		}

		if (!wasRecentlyEnchanted(position)) return

		if (event.particleEffect.type == ParticleTypes.ENCHANTED_HIT
			&& event.count == 4
			&& event.speed == 0.01F
			&& event.offset.y == 0.1f
			&& isEven50Spread
		) {
			burrows[position] = BurrowType.START
		}
		if (event.particleEffect.type == ParticleTypes.CRIT
			&& event.count == 3
			&& event.speed == 0.01F
			&& event.offset.y == 0.1F
			&& isEven50Spread
		) {
			burrows[position] = BurrowType.MOB
		}
		if (event.particleEffect.type == ParticleTypes.DRIPPING_LAVA
			&& event.count == 2
			&& event.speed == 0.01F
			&& event.offset.y == 0.1F
			&& event.offset.x == 0.35F && event.offset.z == 0.35f
		) {
			burrows[position] = BurrowType.TREASURE
		}
	}

	@Subscribe
	fun onRender(event: WorldRenderLastEvent) {
		if (!DianaWaypoints.TConfig.nearbyWaypoints) return
		renderInWorld(event) {
			for ((location, burrow) in burrows) {
				val color = when (burrow) {
					BurrowType.START -> Color.ofRGBA(.2f, .8f, .2f, 0.4f)
					BurrowType.MOB -> Color.ofRGBA(0.3f, 0.4f, 0.9f, 0.4f)
					BurrowType.TREASURE -> Color.ofRGBA(1f, 0.7f, 0.2f, 0.4f)
				}
				block(location, color.color)
			}
		}
	}

	@Subscribe
	fun onSwapWorld(worldReadyEvent: WorldReadyEvent) {
		burrows.clear()
		recentEnchantParticles.clear()
		recentlyDugBurrows.clear()
		lastBlockClick = null
	}

	fun onBlockClick(blockPos: BlockPos) {
		if (!DianaWaypoints.TConfig.nearbyWaypoints) return
		burrows.remove(blockPos)
		lastBlockClick = blockPos
	}
}

fun Position.toBlockPos(): BlockPos {
	return BlockPos(Mth.floor(x()), Mth.floor(y()), Mth.floor(z()))
}
