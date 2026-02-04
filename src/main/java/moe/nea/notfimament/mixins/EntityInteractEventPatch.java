
package moe.nea.notfimament.mixins;

import moe.nea.notfimament.events.EntityInteractionEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class EntityInteractEventPatch {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Player player, Entity target, CallbackInfo ci) {
        EntityInteractionEvent.Companion.publish(new EntityInteractionEvent(EntityInteractionEvent.InteractionKind.ATTACK, target, InteractionHand.MAIN_HAND));
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void onInteract(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EntityInteractionEvent.Companion.publish(new EntityInteractionEvent(EntityInteractionEvent.InteractionKind.INTERACT, entity, hand));
    }

    @Inject(method = "interactAt", at = @At("HEAD"))
    private void onInteractAtLocation(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EntityInteractionEvent.Companion.publish(new EntityInteractionEvent(EntityInteractionEvent.InteractionKind.INTERACT_AT_LOCATION, entity, hand));
    }

}
