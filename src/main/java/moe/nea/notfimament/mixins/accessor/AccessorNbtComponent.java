package moe.nea.notfimament.mixins.accessor;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CustomData.class)
public interface AccessorNbtComponent {
	@Accessor("tag")
	CompoundTag getUnsafeNbt_firmament();
}
