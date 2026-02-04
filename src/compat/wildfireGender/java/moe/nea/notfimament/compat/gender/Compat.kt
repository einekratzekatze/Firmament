package moe.nea.notfimament.compat.gender

import net.fabricmc.loader.api.FabricLoader
import moe.nea.notfimament.util.compatloader.CompatMeta
import moe.nea.notfimament.util.compatloader.ICompatMeta

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return FabricLoader.getInstance().isModLoaded("wildfire_gender")
	}

}
