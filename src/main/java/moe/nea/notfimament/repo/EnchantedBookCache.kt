package moe.nea.notfimament.repo

import io.github.moulberry.repo.IReloadable
import io.github.moulberry.repo.NEURepository
import moe.nea.notfimament.util.SkyblockId
import moe.nea.notfimament.util.removeColorCodes
import moe.nea.notfimament.util.skyblockId

class EnchantedBookCache : IReloadable {
	var byName: Map<String, SkyblockId> = mapOf()
	override fun reload(repo: NEURepository) {
		byName = repo.items.items.values
			.filter { it.displayName.endsWith("Enchanted Book") }
			.associate { it.lore.first().removeColorCodes() to it.skyblockId }
	}
}
