package moe.nea.notfimament.compat.rei

import com.google.auto.service.AutoService
import me.shedaniel.math.impl.PointHelper
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.gui.widgets.Slot
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import moe.nea.notfimament.util.HoveredItemStackProvider
import moe.nea.notfimament.util.compatloader.CompatLoader

@AutoService(HoveredItemStackProvider::class)
@CompatLoader.RequireMod("roughlyenoughitems")
class ScreenRegistryHoveredItemStackProvider : HoveredItemStackProvider {
	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		val entryStack = ScreenRegistry.getInstance().getFocusedStack(screen, PointHelper.ofMouse())
			?: return null
		return entryStack.value as? ItemStack ?: entryStack.cheatsAs().value
	}
}

@AutoService(HoveredItemStackProvider::class)
@CompatLoader.RequireMod("roughlyenoughitems")
class OverlayHoveredItemStackProvider : HoveredItemStackProvider {
	override fun provideHoveredItemStack(screen: Screen): ItemStack? {
		var baseElement: GuiEventListener? = REIRuntime.getInstance().overlay.orElse(null)
		val mx = PointHelper.getMouseFloatingX()
		val my = PointHelper.getMouseFloatingY()
		while (true) {
			if (baseElement is Slot) return baseElement.currentEntry.cheatsAs().value
			if (baseElement !is ContainerEventHandler) return null
			baseElement = baseElement.getChildAt(mx, my).orElse(null)
		}
	}
}
