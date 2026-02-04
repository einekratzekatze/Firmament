
package moe.nea.notfimament.events

import java.util.concurrent.Executor
import net.minecraft.server.packs.resources.ResourceManager

data class EarlyResourceReloadEvent(val resourceManager: ResourceManager, val preparationExecutor: Executor) :
    FirmamentEvent() {
    companion object : FirmamentEventBus<EarlyResourceReloadEvent>()
}
