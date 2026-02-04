package moe.nea.notfimament.mixins.custommodels;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moe.nea.notfimament.features.texturepack.CustomBlockTextures;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public class ReplaceBlockHitSoundPatch {
    @WrapOperation(method = "continueDestroyBlock",
		at = @At(value = "NEW", target = "(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"))
    private SimpleSoundInstance replaceSound(
            SoundEvent sound, SoundSource category, float volume, float pitch,
            RandomSource random, BlockPos pos, Operation<SimpleSoundInstance> original,
            @Local BlockState blockState) {
        var replacement = CustomBlockTextures.getReplacement(blockState, pos);
        if (replacement != null && replacement.getSound() != null) {
            sound = SoundEvent.createVariableRangeEvent(replacement.getSound());
        }
        return original.call(sound, category, volume, pitch, random, pos);
    }
}
