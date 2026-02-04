@file:OptIn(ExperimentalContracts::class)

package moe.nea.notfimament.features.inventory.storageoverlay

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.ChestMenu
import moe.nea.notfimament.util.ifMatches
import moe.nea.notfimament.util.unformattedString

/**
 * A handle representing the state of the "server side" screens.
 */
sealed interface StorageBackingHandle {

    sealed interface HasBackingScreen {
        val handler: ChestMenu
    }

    /**
     * The main storage overview is open. Clicking on a slot will open that page. This page is accessible via `/storage`
     */
    data class Overview(override val handler: ChestMenu) : StorageBackingHandle, HasBackingScreen

    /**
     * An individual storage page is open. This may be a backpack or an enderchest page. This page is accessible via
     * the [Overview] or via `/ec <index + 1>` for enderchest pages.
     */
    data class Page(override val handler: ChestMenu, val storagePageSlot: StoragePageSlot) :
        StorageBackingHandle, HasBackingScreen

    companion object {
        private val enderChestName = "^Ender Chest (?:✦ )?\\(([1-9])/[1-9]\\)$".toRegex()
        private val backPackName = "^.+Backpack (?:✦ )?\\(Slot #([0-9]+)\\)$".toRegex()

        /**
         * Parse a screen into a [StorageBackingHandle]. If this returns null it means that the screen is not
         * representable as a [StorageBackingHandle], meaning another screen is open, for example the enderchest icon
         * selection screen.
         */
        @OptIn(ExperimentalContracts::class)
        fun fromScreen(screen: Screen?): StorageBackingHandle? {
	        contract {
		        returnsNotNull() implies (screen != null)
	        }
            if (screen == null) return null
            if (screen !is ContainerScreen) return null
            val title = screen.title.unformattedString
            if (title == "Storage") return Overview(screen.menu)
            return title.ifMatches(enderChestName) {
                Page(screen.menu, StoragePageSlot.ofEnderChestPage(it.groupValues[1].toInt()))
            } ?: title.ifMatches(backPackName) {
                Page(screen.menu, StoragePageSlot.ofBackPackPage(it.groupValues[1].toInt()))
            }
        }
    }
}
