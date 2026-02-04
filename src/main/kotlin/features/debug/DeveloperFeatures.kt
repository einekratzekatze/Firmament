package moe.nea.notfimament.features.debug

import java.io.File
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.Mixin
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.absolute
import kotlin.io.path.exists
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import moe.nea.notfimament.Firmament
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.DebugInstantiateEvent
import moe.nea.notfimament.events.TickEvent
import moe.nea.notfimament.init.MixinPlugin
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.TimeMark
import moe.nea.notfimament.util.asm.AsmAnnotationUtil
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.iterate

object DeveloperFeatures {
	val DEVELOPER_SUBCOMMAND: String = "dev"
	val identifier: String
		get() = "developer"

	val gradleDir =
		Path.of(".").absolute()
			.iterate { it.parent }
			.find { it.resolve("settings.gradle.kts").exists() }

	@Config
	object TConfig : ManagedConfig("developer", Category.DEV) {
		val autoRebuildResources by toggle("auto-rebuild") { false }
	}

	var missingTranslations: Set<String>? = null

	@JvmStatic
	fun hookMissingTranslations(missingTranslations: Set<String>) {
		this.missingTranslations = missingTranslations
	}

	@Subscribe
	fun loadAllMixinClasses(event: DebugInstantiateEvent) {
		val allMixinClasses = mutableSetOf<String>()
		MixinPlugin.instances.forEach { plugin ->
			val prefix = plugin.mixinPackage + "."
			val classes = plugin.mixins.map { prefix + it }
			allMixinClasses.addAll(classes)
			for (cls in classes) {
				val targets = javaClass.classLoader.getResourceAsStream("${cls.replace(".", "/")}.class").use {
					val node = ClassNode()
					ClassReader(it).accept(node, 0)
					val mixins = mutableListOf<Mixin>()
					(node.visibleAnnotations.orEmpty() + node.invisibleAnnotations.orEmpty()).forEach {
						val annotationType = Type.getType(it.desc)
						val mixinType = Type.getType(Mixin::class.java)
						if (mixinType == annotationType) {
							mixins.add(AsmAnnotationUtil.createProxy(Mixin::class.java, it))
						}
					}
					mixins.flatMap { it.targets.toList() } + mixins.flatMap { it.value.map { it.java.name } }
				}
				for (target in targets)
					try {
						Firmament.logger.debug("Loading ${target} to force instantiate ${cls}")
						Class.forName(target, true, javaClass.classLoader)
					} catch (ex: Throwable) {
						Firmament.logger.error("Could not load class ${target} that has been mixind by $cls", ex)
					}
			}
		}
		Firmament.logger.info("Forceloaded all Firmament mixins:")
		val applied = MixinPlugin.instances.flatMap { it.appliedMixins }.toSet()
		applied.forEach { Firmament.logger.info(" - ${it}") }
		require(allMixinClasses == applied)
	}

	@Subscribe
	fun dumpMissingTranslations(tickEvent: TickEvent) {
		val toDump = missingTranslations ?: return
		missingTranslations = null
		File("missing_translations.json").outputStream().use {
			Firmament.json.encodeToStream(toDump.associateWith { "Mis" + "sing translation" }, it)
		}
	}

	@JvmStatic
	fun hookOnBeforeResourceReload(client: Minecraft): CompletableFuture<Void> {
		val reloadFuture = if (TConfig.autoRebuildResources && Firmament.DEBUG && gradleDir != null) {
			val builder = ProcessBuilder("./gradlew", ":processResources")
			builder.directory(gradleDir.toFile())
			builder.inheritIO()
			val process = builder.start()
			MC.sendChat(Component.translatable("firmament.dev.resourcerebuild.start"))
			val startTime = TimeMark.now()
			process.toHandle().onExit().thenApply {
				MC.sendChat(
					Component.translatableEscape(
						"firmament.dev.resourcerebuild.done",
						startTime.passedTime()
					)
				)
				Unit
			}
		} else {
			CompletableFuture.completedFuture(Unit)
		}
		return reloadFuture.thenCompose { client.reloadResourcePacks() }
	}
}

