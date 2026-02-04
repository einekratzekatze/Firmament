package moe.nea.notfimament.features.texturepack

import moe.nea.notfimament.util.compatloader.CompatMeta
import moe.nea.notfimament.util.compatloader.ICompatMeta

@CompatMeta
object Compat : ICompatMeta {
	override fun shouldLoad(): Boolean {
		return true
	}
}
