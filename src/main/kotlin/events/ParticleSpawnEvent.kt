

package moe.nea.notfimament.events

import org.joml.Vector3f
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.world.phys.Vec3

data class ParticleSpawnEvent(
    val particleEffect: ParticleOptions,
    val position: Vec3,
    val offset: Vector3f,
    val longDistance: Boolean,
    val count: Int,
    val speed: Float,
) : FirmamentEvent.Cancellable() {
    companion object : FirmamentEventBus<ParticleSpawnEvent>()
}
