package moe.nea.notfimament.compat.sodium

import moe.nea.notfimament.util.compatloader.CompatMeta
import moe.nea.notfimament.util.compatloader.ICompatMeta
import net.fabricmc.loader.api.FabricLoader

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return FabricLoader.getInstance().isModLoaded("sodium")
	}
}
