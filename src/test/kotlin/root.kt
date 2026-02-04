package moe.nea.notfimament.test

import net.minecraft.server.Bootstrap
import net.minecraft.SharedConstants
import moe.nea.notfimament.util.TimeMark

object FirmTestBootstrap {
	val loadStart = TimeMark.now()

	init {
		println("Bootstrap started at $loadStart")
	}

	init {
		SharedConstants.tryDetectVersion()
		Bootstrap.bootStrap()
	}

	val loadEnd = TimeMark.now()

	val loadDuration = loadStart.passedAt(loadEnd)

	init {
		println("Bootstrap completed at $loadEnd after $loadDuration")
	}

	@JvmStatic
	fun bootstrapMinecraft() {
	}
}
