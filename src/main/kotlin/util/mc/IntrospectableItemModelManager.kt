package moe.nea.notfimament.util.mc

import net.minecraft.resources.Identifier

interface IntrospectableItemModelManager {
	fun hasModel_firmament(identifier: Identifier): Boolean
}
