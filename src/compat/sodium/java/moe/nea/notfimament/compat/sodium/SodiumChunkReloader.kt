package moe.nea.notfimament.compat.sodium

import moe.nea.notfimament.mixins.sodium.accessor.AccessorSodiumWorldRenderer
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer

class SodiumChunkReloader : Runnable {
    override fun run() {
        (SodiumWorldRenderer.instanceNullable() as? AccessorSodiumWorldRenderer)
            ?.renderSectionManager_firmament
            ?.markGraphDirty()
    }
}
