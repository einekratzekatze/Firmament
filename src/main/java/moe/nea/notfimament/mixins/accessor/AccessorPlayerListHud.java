package moe.nea.notfimament.mixins.accessor;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Comparator;
import java.util.List;

@Mixin(PlayerTabOverlay.class)
public interface AccessorPlayerListHud {

	@Accessor("PLAYER_COMPARATOR")
	static Comparator<PlayerInfo> getEntryOrdering() {
		throw new AssertionError();
	}

	@Invoker("getPlayerInfos")
	List<PlayerInfo> collectPlayerEntries_firmament();

	@Accessor("footer")
	@Nullable Component getFooter_firmament();

	@Accessor("header")
	@Nullable Component getHeader_firmament();

}
