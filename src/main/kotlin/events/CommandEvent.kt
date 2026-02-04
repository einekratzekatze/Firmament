

package moe.nea.notfimament.events

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandBuildContext
import moe.nea.notfimament.commands.CaseInsensitiveLiteralCommandNode
import moe.nea.notfimament.commands.DefaultSource
import moe.nea.notfimament.commands.literal
import moe.nea.notfimament.commands.thenLiteral

data class CommandEvent(
    val dispatcher: CommandDispatcher<DefaultSource>,
    val ctx: CommandBuildContext,
    val serverCommands: CommandDispatcher<*>?,
) : FirmamentEvent() {
    companion object : FirmamentEventBus<CommandEvent>()

    /**
     * Register subcommands to `/firm`. For new top level commands use [CommandEvent]. Cannot be used to register
     * subcommands to other commands.
     */
    data class SubCommand(
        val builder: CaseInsensitiveLiteralCommandNode.Builder<DefaultSource>,
        val commandRegistryAccess: CommandBuildContext,
    ) : FirmamentEvent() {
        companion object : FirmamentEventBus<SubCommand>()

        fun subcommand(name: String, block: CaseInsensitiveLiteralCommandNode.Builder<DefaultSource>.() -> Unit) {
            builder.thenLiteral(name, block)
        }
    }

    fun deleteCommand(name: String) {
        dispatcher.root.children.removeIf { it.name.equals(name, ignoreCase = false) }
        serverCommands?.root?.children?.removeIf { it.name.equals(name, ignoreCase = false) }
    }

    fun register(
        name: String,
        block: CaseInsensitiveLiteralCommandNode.Builder<DefaultSource>.() -> Unit
    ): LiteralCommandNode<DefaultSource> {
        return dispatcher.register(literal(name, block))
    }
}
