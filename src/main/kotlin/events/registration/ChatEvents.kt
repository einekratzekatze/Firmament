package moe.nea.notfimament.events.registration

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import moe.nea.notfimament.events.AllowChatEvent
import moe.nea.notfimament.events.AttackBlockEvent
import moe.nea.notfimament.events.JoinServerEvent
import moe.nea.notfimament.events.ModifyChatEvent
import moe.nea.notfimament.events.ProcessChatEvent
import moe.nea.notfimament.events.UseBlockEvent
import moe.nea.notfimament.events.UseItemEvent

private var lastReceivedMessage: Component? = null

fun registerFirmamentEvents() {
	ClientReceiveMessageEvents.ALLOW_CHAT.register(ClientReceiveMessageEvents.AllowChat { message, signedMessage, sender, params, receptionTimestamp ->
		lastReceivedMessage = message
		!ProcessChatEvent.publish(ProcessChatEvent(message, false)).cancelled
			&& !AllowChatEvent.publish(AllowChatEvent(message)).cancelled
	})
	ClientReceiveMessageEvents.ALLOW_GAME.register(ClientReceiveMessageEvents.AllowGame { message, overlay ->
		lastReceivedMessage = message
		overlay || (!ProcessChatEvent.publish(ProcessChatEvent(message, false)).cancelled &&
			!AllowChatEvent.publish(AllowChatEvent(message)).cancelled)
	})
	ClientReceiveMessageEvents.MODIFY_GAME.register(ClientReceiveMessageEvents.ModifyGame { message, overlay ->
		if (overlay) message
		else ModifyChatEvent.publish(ModifyChatEvent(message)).replaceWith
	})
	ClientReceiveMessageEvents.GAME_CANCELED.register(ClientReceiveMessageEvents.GameCanceled { message, overlay ->
		if (!overlay && lastReceivedMessage !== message) {
			ProcessChatEvent.publish(ProcessChatEvent(message, true))
		}
	})
	ClientReceiveMessageEvents.CHAT_CANCELED.register(ClientReceiveMessageEvents.ChatCanceled { message, signedMessage, sender, params, receptionTimestamp ->
		if (lastReceivedMessage !== message) {
			ProcessChatEvent.publish(ProcessChatEvent(message, true))
		}
	})

	AttackBlockCallback.EVENT.register(AttackBlockCallback { player, world, hand, pos, direction ->
		if (AttackBlockEvent.publish(AttackBlockEvent(player, world, hand, pos, direction)).cancelled)
			InteractionResult.FAIL
		else InteractionResult.PASS
	})
	UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
		if (UseBlockEvent.publish(UseBlockEvent(player, world, hand, hitResult)).cancelled)
			InteractionResult.FAIL
		else InteractionResult.PASS
	})
	UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
		if (UseItemEvent.publish(UseItemEvent(player, world, hand)).cancelled)
			InteractionResult.FAIL
		else InteractionResult.PASS
	})
	UseItemCallback.EVENT.register(UseItemCallback { playerEntity, world, hand ->
		if (UseItemEvent.publish(UseItemEvent(playerEntity, world, hand)).cancelled) InteractionResult.FAIL
		else InteractionResult.PASS
	})
	ClientPlayConnectionEvents.JOIN.register { networkHandler, packetSender, _ ->
		JoinServerEvent.publish(JoinServerEvent(networkHandler, packetSender))
	}
}
