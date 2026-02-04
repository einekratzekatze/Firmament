package moe.nea.notfimament.compat.rei

import com.mojang.serialization.Codec
import me.shedaniel.rei.api.common.entry.EntrySerializer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import moe.nea.notfimament.repo.SBItemStack

object NEUItemEntrySerializer : EntrySerializer<SBItemStack> {
	override fun codec(): Codec<SBItemStack> {
		return SBItemStack.CODEC
	}

	override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SBItemStack> {
		return SBItemStack.PACKET_CODEC.cast()
	}
}
