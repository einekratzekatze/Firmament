package moe.nea.notfimament.features.chat

import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import kotlin.concurrent.thread
import net.minecraft.SharedConstants
import net.minecraft.commands.BrigadierExceptions
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.get
import moe.nea.notfimament.commands.suggestsList
import moe.nea.notfimament.commands.thenArgument
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.events.MaskCommands
import moe.nea.notfimament.repo.RepoManager
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig
import moe.nea.notfimament.util.tr

object AutoCompletions {

	@Config
	object TConfig : ManagedConfig(identifier, Category.CHAT) {
		val provideWarpTabCompletion by toggle("warp-complete") { true }
		val replaceWarpIsByWarpIsland by toggle("warp-is") { true }
	}

	val identifier: String
		get() = "auto-completions"

	@Subscribe
	fun onMaskCommands(event: MaskCommands) {
		if (TConfig.provideWarpTabCompletion) {
			event.mask("warp")
		}
	}

	@Subscribe
	fun onCommandEvent(event: CommandEvent) {
		if (!TConfig.provideWarpTabCompletion) return
		event.deleteCommand("warp")
		event.register("warp") {
			thenArgument("to", string()) { toArg ->
				suggestsList {
					RepoManager.neuRepo.constants?.islands?.warps?.flatMap { listOf(it.warp) + it.aliases } ?: listOf()
				}
				thenExecute {
					val warpName = get(toArg)
					if (warpName == "is" && TConfig.replaceWarpIsByWarpIsland) {
						MC.sendCommand("warp island")
					} else {
						redirectToServer()
					}
				}
			}
		}
	}

	fun CommandContext<*>.redirectToServer() {
		val message = tr(
			"firmament.warp.auto-complete.internal-throw",
			"This is an internal syntax exception that should not show up in gameplay, used to pass on a command to the server"
		)
		throw CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand(), message)
	}
}
