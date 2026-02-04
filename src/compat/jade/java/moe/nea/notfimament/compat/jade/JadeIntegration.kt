package moe.nea.notfimament.compat.jade

import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.SkyblockServerUpdateEvent
import moe.nea.notfimament.repo.MiningRepoData
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.util.ErrorUtil
import net.minecraft.world.level.block.Block
import moe.nea.notfimament.events.ReloadRegistrationEvent
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig

object JadeIntegration {
	@Config
	object TConfig : ManagedConfig("jade-integration", Category.INTEGRATIONS) {
		val miningProgress by toggle("progress") { true }
		val blockDetection by toggle("blocks") { true }
	}

	var customBlocks: Map<Block, MiningRepoData.CustomMiningBlock> = mapOf()

	fun refreshBlockInfo() {
		if (!isOnMiningIsland()) {
			customBlocks = mapOf()
			return
		}
		val blocks = RepoManager.miningData.customMiningBlocks
			.flatMap { customBlock ->
				// TODO: add a lifted helper method for this
				customBlock.blocks189.filter { it.isCurrentlyActive }
					.mapNotNull { it.block }
					.map { customBlock to it }
			}
			.groupBy { it.second }
		customBlocks = blocks.mapNotNull { (block, customBlocks) ->
			val singleMatch =
				ErrorUtil.notNullOr(customBlocks.singleOrNull()?.first,
				                    "Two custom blocks both want to supply custom mining behaviour for $block.") { return@mapNotNull null }
			block to singleMatch
		}.toMap()
	}

	@Subscribe
	fun onRepoReload(event: ReloadRegistrationEvent) {
		event.repo.registerReloadListener { refreshBlockInfo() }
	}

	@Subscribe
	fun onWorldSwap(event: SkyblockServerUpdateEvent) {
		refreshBlockInfo()
	}
}
