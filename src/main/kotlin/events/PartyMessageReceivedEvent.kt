package moe.nea.notfimament.events

data class PartyMessageReceivedEvent(
	val from: ProcessChatEvent,
	val message: String,
	val name: String,
) : FirmamentEvent() {
	companion object : FirmamentEventBus<PartyMessageReceivedEvent>()
}
