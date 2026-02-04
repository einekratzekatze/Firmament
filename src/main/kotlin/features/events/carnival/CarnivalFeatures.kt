
package moe.nea.notfimament.features.events.carnival

import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig

object CarnivalFeatures {
	@Config
	object TConfig : ManagedConfig(identifier, Category.EVENTS) {
        val enableBombSolver by toggle("bombs-solver") { true }
        val displayTutorials by toggle("tutorials") { true }
    }

    val identifier: String
        get() = "carnival"
}
