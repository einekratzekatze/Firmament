package moe.nea.notfimament.features.world

import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.commands.thenExecute
import moe.nea.notfimament.events.CommandEvent
import moe.nea.notfimament.events.ReloadRegistrationEvent
import moe.nea.notfimament.util.MoulConfigUtils
import moe.nea.notfimament.util.ScreenUtil

object NPCWaypoints {

    var allNpcWaypoints = listOf<NavigableWaypoint>()

    @Subscribe
    fun onRepoReloadRegistration(event: ReloadRegistrationEvent) {
        event.repo.registerReloadListener {
            allNpcWaypoints = it.items.items.values
                .asSequence()
                .filter { !it.island.isNullOrBlank() }
                .map {
                    NavigableWaypoint.NPCWaypoint(it)
                }
                .toList()
        }
    }

    @Subscribe
    fun onOpenGui(event: CommandEvent.SubCommand) {
        event.subcommand("npcs") {
            thenExecute {
                ScreenUtil.setScreenLater(MoulConfigUtils.loadScreen(
                    "npc_waypoints",
                    NpcWaypointGui(allNpcWaypoints),
                    null))
            }
        }
    }


}
