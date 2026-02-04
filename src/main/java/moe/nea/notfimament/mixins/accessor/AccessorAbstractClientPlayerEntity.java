
package moe.nea.notfimament.mixins.accessor;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AccessorAbstractClientPlayerEntity {
    @Accessor("playerInfo")
    void setPlayerListEntry_firmament(PlayerInfo playerListEntry);
}
