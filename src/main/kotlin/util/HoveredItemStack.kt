package moe.nea.notfimament.util

import com.google.auto.service.AutoService
import kotlin.jvm.optionals.getOrNull
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.api.v1.FirmamentAPI
import moe.nea.notfimament.mixins.accessor.AccessorHandledScreen
import moe.nea.notfimament.util.compatloader.CompatLoader

interface HoveredItemStackProvider : Comparable<HoveredItemStackProvider> {
	fun provideHoveredItemStack(screen: Screen): ItemStack?
	override fun compareTo(other: HoveredItemStackProvider): Int {
		return compareValues(this.prio, other.prio)
	}

	val prio: Int get() = 0

	companion object : CompatLoader<HoveredItemStackProvider>(HoveredItemStackProvider::class) {
		val sorted = HoveredItemStackProvider.allValidInstances.sorted()
	}
}

@AutoService(HoveredItemStackProvider::class)
class VanillaScreenProvider : HoveredItemStackProvider {

	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		if (screen !is AccessorHandledScreen) return null
		val vanillaSlot = screen.focusedSlot_Firmament?.item
		return vanillaSlot
	}

	override val prio: Int
		get() = -1
}

@AutoService(HoveredItemStackProvider::class)
class FirmamentStackScreenProvider : HoveredItemStackProvider {
	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		return FirmamentAPI.getInstance()
			.hoveredItemWidget
			.getOrNull()
			?.itemStack
	}
}

val Screen.focusedItemStack: ItemStack?
	get() =
		HoveredItemStackProvider.sorted
			.firstNotNullOfOrNull { it.provideHoveredItemStack(this)?.takeIf { !it.isEmpty } }
