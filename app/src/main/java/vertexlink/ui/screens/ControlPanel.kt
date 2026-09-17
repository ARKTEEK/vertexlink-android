package vertexlink.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vertexlink.ui.theme.VertexColors
import vertexlink.controller.MouseController
import vertexlink.ui.components.common.IconRoundButton
import vertexlink.ui.modifier.touchpadInput

@Composable
fun ControlPanel(
  onDisconnect: () -> Unit,
  mouseController: MouseController,
  modifier: Modifier = Modifier
) {
  var showKeyboard by remember { mutableStateOf(false) }
  var showVolumePopup by remember { mutableStateOf(false) }
  var showInfoPopup by remember { mutableStateOf(false) }
  var volume by remember { mutableStateOf(50f) }

  val context = LocalContext.current

  DisposableEffect(Unit) {
    val activity = context as? Activity
    val previousOrientation = activity?.requestedOrientation
    val window = activity?.window

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    if (window != null) {
      val insetsController = WindowCompat.getInsetsController(window, window.decorView)
      insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    onDispose {
      activity?.requestedOrientation =
        previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
      if (window != null) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.show(WindowInsetsCompat.Type.systemBars())
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VertexColors.BgSurfaceMid)
      .padding(16.dp)
  ) {
    Box(
      modifier = Modifier
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

    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      horizontalAlignment = Alignment.End,
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp)
    ) {
      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.Logout,
        contentDescription = "Disconnect",
        onClick = onDisconnect
      )

      IconRoundButton(
        icon = Icons.Outlined.Keyboard,
        contentDescription = "Toggle keyboard",
        onClick = {
          showKeyboard = !showKeyboard
          if (showKeyboard) {
            showVolumePopup = false
            showInfoPopup = false
          }
        }
      )

      IconRoundButton(
        icon = Icons.Outlined.ContentPaste,
        contentDescription = "Sync clipboard",
        onClick = { }
      )

      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.VolumeUp,
        contentDescription = "Volume",
        onClick = {
          showVolumePopup = !showVolumePopup
          if (showVolumePopup) {
            showKeyboard = false
            showInfoPopup = false
          }
        }
      )

      IconRoundButton(
        icon = Icons.Outlined.Info,
        contentDescription = "Gestures info",
        onClick = {
          showInfoPopup = !showInfoPopup
          if (showInfoPopup) {
            showKeyboard = false
            showVolumePopup = false
          }
        }
      )
    }

    if (showVolumePopup) {
      VolumePopup(
        volume = volume,
        onVolumeChange = { volume = it },
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 72.dp)
      )
    }

    if (showInfoPopup) {
      GesturesInfoPopup(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 72.dp)
      )
    }
  }
}

@Composable
private fun VolumePopup(
  volume: Float,
  onVolumeChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .widthIn(min = 220.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(VertexColors.BgSurfaceHigh.copy(alpha = 0.95f))
      .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Icon(
      imageVector = Icons.Outlined.VolumeUp,
      contentDescription = null,
      tint = VertexColors.TextSecondary
    )

    Spacer(modifier = Modifier.width(8.dp))

    Slider(
      value = volume,
      onValueChange = onVolumeChange,
      valueRange = 0f..100f,
      colors = SliderDefaults.colors(
        thumbColor = VertexColors.AccentPrimary,
        activeTrackColor = VertexColors.AccentPrimary,
        inactiveTrackColor = VertexColors.BorderSubtle
      ),
      modifier = Modifier.width(140.dp)
    )
  }
}

@Composable
private fun GesturesInfoPopup(
  modifier: Modifier = Modifier
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = modifier
      .widthIn(min = 200.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(VertexColors.BgSurfaceHigh.copy(alpha = 0.95f))
      .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(16.dp))
      .padding(12.dp)
  ) {
    Text(
      text = "Available Gestures",
      style = MaterialTheme.typography.titleSmall,
      color = VertexColors.TextPrimary
    )
    Text(
      text = "1 Finger Drag: Move cursor",
      style = MaterialTheme.typography.bodySmall,
      color = VertexColors.TextSecondary
    )
    Text(
      text = "1 Finger Tap: Left click",
      style = MaterialTheme.typography.bodySmall,
      color = VertexColors.TextSecondary
    )
    Text(
      text = "1 Finger Tap and Hold: Dragging",
      style = MaterialTheme.typography.bodySmall,
      color = VertexColors.TextSecondary
    )
    Text(
      text = "2 Finger Tap: Right click",
      style = MaterialTheme.typography.bodySmall,
      color = VertexColors.TextSecondary
    )
  }
}