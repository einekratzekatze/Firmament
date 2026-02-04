
package moe.nea.notfimament.events

import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.level.Level

data class UseBlockEvent(val player: Player, val world: Level, val hand: InteractionHand, val hitResult: BlockHitResult) : FirmamentEvent.Cancellable() {
    companion object : FirmamentEventBus<UseBlockEvent>()
}
