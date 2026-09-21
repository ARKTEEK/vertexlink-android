package vertexlink.ui.components.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.controller.MouseController
import vertexlink.ui.modifier.touchpadInput

@Composable
fun TouchpadArea(
  mouseController: MouseController,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .border(
        width = 1.dp,
        color = VertexColors.BorderSubtle,
        shape = RoundedCornerShape(16.dp)
      )
      .clip(RoundedCornerShape(16.dp))
      .background(VertexColors.BgSurfaceLow)
      .touchpadInput(
        onMouseMove = mouseController::sendMouseMove,
        onLeftClick = mouseController::sendLeftClick,
        onRightClick = mouseController::sendRightClick,
        onMouseLeftDown = mouseController::sendLeftDown,
        onMouseLeftUp = mouseController::sendLeftUp
      )
  ) {
    Text(
      text = "Touchpad",
      style = MaterialTheme.typography.labelSmall,
      color = VertexColors.TextSecondary,
      modifier = Modifier.padding(16.dp)
    )
  }
}