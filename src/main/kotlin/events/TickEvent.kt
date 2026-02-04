

package moe.nea.notfimament.events

data class TickEvent(val tickCount: Int) : FirmamentEvent() {
	// TODO: introduce a client / server tick system.
	//       client ticks should ignore the game state
	//       server ticks should per-tick count packets received by the server
    companion object : FirmamentEventBus<TickEvent>()
}
