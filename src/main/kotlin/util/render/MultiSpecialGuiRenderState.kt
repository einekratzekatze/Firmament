package moe.nea.notfimament.util.render

import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.gui.render.state.GuiRenderState
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState
import net.minecraft.client.renderer.MultiBufferSource

abstract class MultiSpecialGuiRenderState : PictureInPictureRenderState {
	// I wish i had manifolds @Self type here... Maybe i should switch to java after all :(
	abstract fun createRenderer(vertexConsumers: MultiBufferSource.BufferSource): MultiSpecialGuiRenderer<out MultiSpecialGuiRenderState>
	abstract val x1: Int
	abstract val x2: Int
	abstract val y1: Int
	abstract val y2: Int
	abstract val scale: Float
	abstract val bounds: ScreenRectangle?
	abstract val scissorArea: ScreenRectangle?
	override fun x0(): Int = x1

	override fun x1(): Int = x2

	override fun y0(): Int = y1

	override fun y1(): Int = y2

	override fun scale(): Float = scale

	override fun scissorArea(): ScreenRectangle? = scissorArea

	override fun bounds(): ScreenRectangle? = bounds

}

abstract class MultiSpecialGuiRenderer<T : MultiSpecialGuiRenderState>(
	vertexConsumers: MultiBufferSource.BufferSource
) : PictureInPictureRenderer<T>(vertexConsumers) {
	var wasUsedThisFrame = false
	fun consumeRender(): Boolean {
		return wasUsedThisFrame.also { wasUsedThisFrame = false }
	}

	override fun blitTexture(element: T, state: GuiRenderState) {
		wasUsedThisFrame = true
		super.blitTexture(element, state)
	}
}
