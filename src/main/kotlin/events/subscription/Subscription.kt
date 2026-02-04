
package moe.nea.notfimament.events.subscription

import moe.nea.notfimament.events.FirmamentEvent
import moe.nea.notfimament.events.FirmamentEventBus


data class Subscription<T : FirmamentEvent>(
    val owner: Any,
    val invoke: (T) -> Unit,
    val eventBus: FirmamentEventBus<T>,
    val methodName: String,
)
