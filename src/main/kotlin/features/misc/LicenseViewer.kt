package moe.nea.notfimament.features.misc

import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.xml.Bind
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.util.ErrorUtil
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.MoulConfigUtils
import moe.nea.notfimament.util.ScreenUtil
import moe.nea.notfimament.util.tr

object LicenseViewer {
	@Serializable
	data class Software(
		val licenses: List<License> = listOf(),
		val webPresence: String? = null,
		val projectName: String,
		val projectDescription: String? = null,
		val developers: List<Developer> = listOf(),
	) {

		@Bind
		fun hasWebPresence() = webPresence != null

		@Bind
		fun webPresence() = Component.literal(webPresence ?: "<no web presence>")

		@Bind
		fun open() {
			MC.openUrl(webPresence ?: return)
		}

		@Bind
		fun projectName() = Component.literal(projectName)

		@Bind
		fun projectDescription() = Component.literal(projectDescription ?: "<no project description>")

		@get:Bind("developers")
		@Transient
		val developersO = ObservableList(developers)

		@get:Bind("licenses")
		@Transient
		val licenses0 = ObservableList(licenses)
	}

	@Serializable
	data class Developer(
		val name: String,
		val webPresence: String? = null
	) {

		@Bind("name")
		fun nameT() = Component.literal(name)

		@Bind
		fun open() {
			MC.openUrl(webPresence ?: return)
		}

		@Bind
		fun hasWebPresence() = webPresence != null

		@Bind
		fun webPresence() = Component.literal(webPresence ?: "<no web presence>")
	}

	@Serializable
	data class License(
		val licenseName: String,
		val licenseUrl: String? = null
	) {
		@Bind("name")
		fun nameG() = Component.literal(licenseName)

		@Bind
		fun open() {
			MC.openUrl(licenseUrl ?: return)
		}

		@Bind
		fun hasUrl() = licenseUrl != null

		@Bind
		fun url() = Component.literal(licenseUrl ?: "<no link to license text>")
	}

	data class LicenseList(
		val softwares: List<Software>
	) {
		@get:Bind("softwares")
		val obs = ObservableList(softwares)
	}

	@OptIn(ExperimentalSerializationApi::class)
	val licenses: LicenseList? = ErrorUtil.catch("Could not load licenses") {
		Firmament.json.decodeFromStream<List<Software>?>(
			javaClass.getResourceAsStream("/LICENSES-FIRMAMENT.json") ?: error("Could not find LICENSES-FIRMAMENT.json")
		)?.let { LicenseList(it) }
	}.orNull()

	fun showLicenses() {
		ErrorUtil.catch("Could not display licenses") {
			ScreenUtil.setScreenLater(
				MoulConfigUtils.loadScreen(
					"license_viewer/index", licenses!!, null
				)
			)
		}.or {
			MC.sendChat(
				tr(
					"firmament.licenses.notfound",
					"Could not load licenses. Please check the Firmament source code for information directly."
				)
			)
		}
	}

	@Subscribe
	fun onSubcommand(event: CommandEvent.SubCommand) {
		event.subcommand("licenses") {
			thenExecute {
				showLicenses()
			}
		}
	}
}
