
package moe.nea.notfimament.mixins.customgui;

import moe.nea.notfimament.util.customgui.CoordRememberingSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Slot.class)
public class OriginalSlotCoords implements CoordRememberingSlot {

    @Shadow
    public int x;
    @Shadow
    public int y;
    @Unique
    public int originalX;
    @Unique
    public int originalY;

    @Override
    public void rememberCoords_firmament() {
        this.originalX = this.x;
        this.originalY = this.y;
    }

    @Override
    public void restoreCoords_firmament() {
        this.x = this.originalX;
        this.y = this.originalY;
    }

    @Override
    public int getOriginalX_firmament() {
        return originalX;
    }

    @Override
    public int getOriginalY_firmament() {
        return originalY;
    }
}
