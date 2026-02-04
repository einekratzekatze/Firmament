package moe.nea.notfimament.mixins.render.entitytints;

import moe.nea.notfimament.events.EntityRenderTintEvent;
import moe.nea.notfimament.util.render.TintedOverlayTexture;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateTint implements EntityRenderTintEvent.HasTintRenderState {
	@Unique
	int tint = -1;
	@Unique
	TintedOverlayTexture overlayTexture;
	@Unique
	boolean hasTintOverride = false;

	@Override
	public int getTint_firmament() {
		return tint;
	}

	@Override
	public void setTint_firmament(int i) {
		tint = i;
		hasTintOverride = true;
	}

	@Override
	public boolean getHasTintOverride_firmament() {
		return hasTintOverride;
	}

	@Override
	public void setHasTintOverride_firmament(boolean b) {
		hasTintOverride = b;
	}

	@Override
	public void reset_firmament() {
		hasTintOverride = false;
		overlayTexture = null;
	}

	@Override
	public @Nullable TintedOverlayTexture getOverlayTexture_firmament() {
		return overlayTexture;
	}

	@Override
	public void setOverlayTexture_firmament(@Nullable TintedOverlayTexture tintedOverlayTexture) {
		this.overlayTexture = tintedOverlayTexture;
	}
}
