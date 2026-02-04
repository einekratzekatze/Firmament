package moe.nea.notfimament.compat.iris

import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import util.render.CustomRenderPipelines
import moe.nea.notfimament.annotations.Subscribe
import moe.nea.notfimament.events.ClientInitEvent

object IrisPipelineAssigner {
	@Subscribe
	fun initIrisAssignments(event: ClientInitEvent) {
		val api = IrisApi.getInstance()
		api.assignPipeline(CustomRenderPipelines.GUI_TEXTURED_NO_DEPTH_TRIS, IrisProgram.TEXTURED)
		api.assignPipeline(CustomRenderPipelines.OMNIPRESENT_LINES, IrisProgram.LINES)
		api.assignPipeline(CustomRenderPipelines.COLORED_OMNIPRESENT_QUADS, IrisProgram.BASIC)
		api.assignPipeline(CustomRenderPipelines.CIRCLE_FILTER_TRANSLUCENT_GUI_TRIS, IrisProgram.TEXTURED)
		api.assignPipeline(CustomRenderPipelines.PARALLAX_CAPE_SHADER, IrisProgram.ENTITIES)
	}
}
