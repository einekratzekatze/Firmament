package moe.nea.notfimament.features.mining

import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.platform.MoulConfigPlatform
import io.github.notenoughupdates.moulconfig.xml.Bind
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.repo.MiningRepoData
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.util.MoulConfigUtils
import moe.nea.notfimament.util.SkyBlockIsland

object MiningBlockInfoUi {
	class MiningInfo(miningData: MiningRepoData) {
		@field:Bind("search")
		@JvmField
		var search = ""

		@get:Bind("ores")
		val blocks = miningData.customMiningBlocks.mapTo(ObservableList(mutableListOf())) { OreInfo(it, this) }
	}

	class OreInfo(block: MiningRepoData.CustomMiningBlock, info: MiningInfo) {
		@get:Bind("oreName")
		val oreName = block.name ?: "No Name"

		@get:Bind("blocks")
		val res = ObservableList(block.blocks189.map { BlockInfo(it, info) })
	}

	class BlockInfo(val block: MiningRepoData.Block189, val info: MiningInfo) {
		@get:Bind("item")
		val item = MoulConfigPlatform.wrap(block.block?.let { ItemStack(it) } ?: ItemStack.EMPTY)

		@get:Bind("isSelected")
		val isSelected get() = info.search.let { block.isActiveIn(SkyBlockIsland.forMode(it)) }

		@get:Bind("itemName")
		val itemName get() = item.getDisplayName()

		@get:Bind("restrictions")
		val res = ObservableList(
			if (block.onlyIn != null)
				block.onlyIn.map { " §r- §a${it.userFriendlyName}" }
			else
				listOf("Everywhere")
		)
	}

	fun makeScreen(): Screen {
		return MoulConfigUtils.loadScreen("mining_block_info/index", MiningInfo(RepoManager.miningData), null)
	}
}
