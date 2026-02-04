package moe.nea.notfimament.test.testutil

import com.google.auto.service.AutoService
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import moe.nea.notfimament.test.FirmTestBootstrap

@AutoService(Extension::class)
class AutoBootstrapExtension : Extension, BeforeAllCallback {
	override fun beforeAll(p0: ExtensionContext) {
		FirmTestBootstrap.bootstrapMinecraft()
	}
}
