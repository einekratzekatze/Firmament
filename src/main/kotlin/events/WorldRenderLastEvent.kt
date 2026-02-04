

package moe.nea.notfimament.events

import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack

/**
 * This event is called after all world rendering is done, but before any GUI rendering (including hand) has been done.
 */
data class WorldRenderLastEvent(
    val matrices: PoseStack,
    val tickCounter: Int,
    val camera: CameraRenderState,
    val vertexConsumers: MultiBufferSource.BufferSource,
) : FirmamentEvent() {
    companion object : FirmamentEventBus<WorldRenderLastEvent>()
}
