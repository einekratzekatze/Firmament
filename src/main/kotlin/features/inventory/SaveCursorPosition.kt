package moe.nea.notfimament.features.inventory

import org.lwjgl.glfw.GLFW
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds
import com.mojang.blaze3d.platform.InputConstants
import moe.nea.notfimament.util.MC
import moe.nea.notfimament.util.TimeMark
import moe.nea.notfimament.util.assertNotNullOr
import moe.nea.notfimament.util.data.Config
import moe.nea.notfimament.util.data.ManagedConfig

object SaveCursorPosition {
	val identifier: String
		get() = "save-cursor-position"

	@Config
	object TConfig : ManagedConfig(identifier, Category.INVENTORY) {
		val enable by toggle("enable") { true }
		val tolerance by duration("tolerance", 10.milliseconds, 5000.milliseconds) { 500.milliseconds }
	}

	var savedPositionedP1: Pair<Double, Double>? = null
	var savedPosition: SavedPosition? = null

	data class SavedPosition(
		val middle: Pair<Double, Double>,
		val cursor: Pair<Double, Double>,
		val savedAt: TimeMark = TimeMark.now()
	)

	@JvmStatic
	fun saveCursorOriginal(positionedX: Double, positionedY: Double) {
		savedPositionedP1 = Pair(positionedX, positionedY)
	}

	@JvmStatic
	fun loadCursor(middleX: Double, middleY: Double): Pair<Double, Double>? {
		if (!TConfig.enable) return null
		val lastPosition = savedPosition?.takeIf { it.savedAt.passedTime() < TConfig.tolerance }
		savedPosition = null
		if (lastPosition != null &&
			(lastPosition.middle.first - middleX).absoluteValue < 1 &&
			(lastPosition.middle.second - middleY).absoluteValue < 1
		) {
			InputConstants.grabOrReleaseMouse(
				MC.window,
				InputConstants.CURSOR_NORMAL,
				lastPosition.cursor.first,
				lastPosition.cursor.second
			)
			return lastPosition.cursor
		}
		return null
	}

	@JvmStatic
	fun saveCursorMiddle(middleX: Double, middleY: Double) {
		if (!TConfig.enable) return
		val cursorPos = assertNotNullOr(savedPositionedP1) { return }
		savedPosition = SavedPosition(Pair(middleX, middleY), cursorPos)
	}
}
