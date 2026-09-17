package vertexlink.ui.modifier

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.roundToInt

fun Modifier.touchpadInput(
  onMouseMove: (Int, Int) -> Unit,
  onLeftClick: () -> Unit,
  onRightClick: () -> Unit,
  onMouseLeftDown: () -> Unit,
  onMouseLeftUp: () -> Unit
): Modifier {
  return this.pointerInput(Unit) {
    detectTouchpadGestures(
      onMouseMove = onMouseMove,
      onLeftClick = onLeftClick,
      onRightClick = onRightClick,
      onMouseLeftDown = onMouseLeftDown,
      onMouseLeftUp = onMouseLeftUp
    )
  }
}

private suspend fun PointerInputScope.detectTouchpadGestures(
  onMouseMove: (Int, Int) -> Unit,
  onLeftClick: () -> Unit,
  onRightClick: () -> Unit,
  onMouseLeftDown: () -> Unit,
  onMouseLeftUp: () -> Unit
) {
  val longPressTimeoutMillis = viewConfiguration.longPressTimeoutMillis
  val touchSlop = viewConfiguration.touchSlop

  awaitEachGesture {
    val down = awaitFirstDown(requireUnconsumed = false)
    down.consume()

    val downTimeMillis = System.currentTimeMillis()
    var totalMovement = 0f
    var isPlainMove = false
    var isClickDrag = false
    var secondPointerSeen = false

    while (true) {
      val event = awaitPointerEvent()
      val changes = event.changes

      if (changes.size > 1) {
        secondPointerSeen = true
      }

      val primary = changes.firstOrNull { it.id == down.id } ?: changes.first()

      if (!primary.pressed) {
        if (isClickDrag) {
          onMouseLeftUp()
        } else if (secondPointerSeen) {
          onRightClick()
        } else if (!isPlainMove && totalMovement < touchSlop) {
          onLeftClick()
        }
        break
      }

      val delta = primary.positionChange()
      primary.consume()

      if (!isPlainMove && !isClickDrag) {
        totalMovement += delta.getDistance()
        val heldLongEnough = System.currentTimeMillis() - downTimeMillis >= longPressTimeoutMillis

        if (heldLongEnough && totalMovement < touchSlop) {
          isClickDrag = true
          onMouseLeftDown()
        } else if (totalMovement >= touchSlop) {
          isPlainMove = true
        }
      }

      if (isPlainMove || isClickDrag) {
        onMouseMove(delta.x.roundToInt(), delta.y.roundToInt())
      }
    }
  }
}