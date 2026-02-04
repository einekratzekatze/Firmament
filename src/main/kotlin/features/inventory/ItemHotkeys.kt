package moe.nea.notfimament.features.inventory

import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.HandledScreenKeyPressedEvent
import moe.nea.notfimament.repo.ExpensiveItemCacheApi
import moe.nea.notfimament.repo.HypixelStaticData
import moe.nea.notfimament.repo.ItemCache.asItemStack
import moe.nea.notfimament.repo.ItemCache.isBroken
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.asBazaarStock
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.focusedItemStack
import moe.nea.notfimament.util.skyBlockId
import moe.nea.notfimament.util.skyblock.SBItemUtil.getSearchName

object ItemHotkeys {
	@Config
	object TConfig : ManagedConfig("item-hotkeys", Category.INVENTORY) {
		val openGlobalTradeInterface by keyBindingWithDefaultUnbound("global-trade-interface")
	}

	@OptIn(ExpensiveItemCacheApi::class)
	@Subscribe
	fun onHandledInventoryPress(event: HandledScreenKeyPressedEvent) {
		if (!event.matches(TConfig.openGlobalTradeInterface)) {
			return
		}
		var item = event.screen.focusedItemStack ?: return
		val skyblockId = item.skyBlockId ?: return
		item = RepoManager.getNEUItem(skyblockId)?.asItemStack()?.takeIf { !it.isBroken } ?: item
		if (HypixelStaticData.hasBazaarStock(skyblockId.asBazaarStock)) {
			MC.sendCommand("bz ${item.getSearchName()}")
		} else if (HypixelStaticData.hasAuctionHouseOffers(skyblockId)) {
			MC.sendCommand("ahs ${item.getSearchName()}")
		} else {
			return
		}
		event.cancel()
	}

}
