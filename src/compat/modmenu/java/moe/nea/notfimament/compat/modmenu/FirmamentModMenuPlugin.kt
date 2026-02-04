package moe.nea.notfimament.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import moe.nea.notfimament.gui.config.AllConfigsGui

class FirmamentModMenuPlugin : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { AllConfigsGui.makeScreen(parent = it) }
    }
}
