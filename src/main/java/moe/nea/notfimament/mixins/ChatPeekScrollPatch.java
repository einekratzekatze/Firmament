package moe.nea.notfimament.mixins;

import moe.nea.notfimament.features.fixes.Fixes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class ChatPeekScrollPatch {

	@Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedSlot()I"), cancellable = true)
	public void onHotbarScrollWhilePeeking(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (Fixes.INSTANCE.shouldPeekChat() && Fixes.INSTANCE.shouldScrollPeekedChat()) ci.cancel();
	}

	@ModifyVariable(method = "onScroll", at = @At(value = "STORE"), ordinal = 0)
	public int onGetChatHud(int i) {
		if (Fixes.INSTANCE.shouldPeekChat() && Fixes.INSTANCE.shouldScrollPeekedChat())
			Minecraft.getInstance().gui.getChat().scrollChat(i);
		return i;
	}

}
