

package moe.nea.notfimament.jarvis

import moe.nea.jarvis.api.Jarvis
import moe.nea.jarvis.api.JarvisConfigOption
import moe.nea.jarvis.api.JarvisHud
import moe.nea.jarvis.api.JarvisPlugin
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.gui.config.HudMeta
import moe.nea.notfimament.gui.config.HudMetaHandler
import moe.nea.notfimament.gui.config.storage.FirmamentConfigLoader
import moe.nea.notfimament.util.data.ManagedConfig

class JarvisIntegration : JarvisPlugin {
    override fun getModId(): String =
        Firmament.MOD_ID

    companion object {
        lateinit var jarvis: Jarvis
    }

    override fun onInitialize(jarvis: Jarvis) {
        Companion.jarvis = jarvis
    }

    val configs
        get() = FirmamentConfigLoader.allConfigs.filterIsInstance<ManagedConfig>()


    override fun getAllHuds(): List<JarvisHud> {
        return configs.flatMap { config ->
            config.sortedOptions.mapNotNull { if (it.handler is HudMetaHandler) it.value as HudMeta else null }
        }
    }

    override fun onHudEditorClosed() {
        configs.forEach { it.markDirty() }
    }

    override fun getAllConfigOptions(): List<JarvisConfigOption> {
        return configs.flatMap { config ->
            config.sortedOptions.map {
                object : JarvisConfigOption {
                    override fun title(): Component {
                        return it.labelText
                    }

                    override fun description(): List<Component> {
                        return emptyList()
                    }

                    override fun jumpTo(parentScreen: Screen?): Screen {
                        return config.getConfigEditor(parentScreen)
                    }
                }
            }
        }
    }
}
