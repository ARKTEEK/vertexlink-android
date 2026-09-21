package vertexlink.ui.components.controlpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.ui.components.common.IconRoundButton

@Composable
fun ControlRail(
  isMenuOpen: Boolean,
  onToggleMenu: () -> Unit,
  onDisconnect: () -> Unit,
  onToggleKeyboard: () -> Unit,
  onSyncClipboard: () -> Unit,
  onToggleVolume: () -> Unit,
  onToggleInfo: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)

  val container = if (isMenuOpen) {
    Modifier
      .clip(shape)
      .background(VertexColors.BgSurfaceHigh.copy(alpha = 0.95f))
      .border(1.dp, VertexColors.BorderSubtle, shape)
  } else {
    Modifier
  }

  Column(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .then(container)
      .verticalScroll(rememberScrollState())
      .padding(4.dp)
  ) {
    IconRoundButton(
      icon = if (isMenuOpen) {
        Icons.Outlined.Close
      } else {
        Icons.Outlined.Menu
      },
      contentDescription = "Menu",
      onClick = onToggleMenu
    )

    if (isMenuOpen) {
      IconRoundButton(
        icon = Icons.Outlined.Keyboard,
        contentDescription = "Toggle keyboard",
        onClick = onToggleKeyboard
      )

      IconRoundButton(
        icon = Icons.Outlined.ContentPaste,
        contentDescription = "Sync clipboard",
        onClick = onSyncClipboard
      )

      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.VolumeUp,
        contentDescription = "Volume",
        onClick = onToggleVolume
      )

      IconRoundButton(
        icon = Icons.Outlined.Info,
        contentDescription = "Gestures info",
        onClick = onToggleInfo
      )

      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.Logout,
        contentDescription = "Disconnect",
        onClick = onDisconnect
      )
    }
  }
}