package vertexlink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.ui.components.common.ActionChip
import vertexlink.ui.components.common.IconRoundButton
import vertexlink.ui.components.device.DeviceAvatar
import vertexlink.ui.components.device.DeviceKind

@Composable
fun ControlPanel(
  deviceName: String,
  onDisconnect: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showKeyboard by remember { mutableStateOf(false) }
  var keyboardText by remember { mutableStateOf("") }
  var volume by remember { mutableStateOf(50f) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
      DeviceAvatar(kind = DeviceKind.Desktop, showStatusDot = false, isOnline = true)

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(text = deviceName, style = MaterialTheme.typography.titleMedium)
        Text(text = "Connected", style = MaterialTheme.typography.bodySmall)
      }

      IconRoundButton(
        icon = Icons.AutoMirrored.Outlined.Logout,
        contentDescription = "Disconnect",
        onClick = onDisconnect
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(16.dp))
        .background(VertexColors.BgSurfaceMid)
        .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(16.dp))
        .pointerInput(Unit) {
          detectTapGestures(
            onTap = { },
            onLongPress = { }
          )
        }
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
          }
        }
    ) {
      Text(
        text = "Touchpad",
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(12.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      ActionChip(
        icon = Icons.Outlined.Keyboard,
        label = "Keyboard",
        active = showKeyboard,
        onClick = { showKeyboard = !showKeyboard },
        modifier = Modifier.weight(1f)
      )

      ActionChip(
        icon = Icons.Outlined.ContentPaste,
        label = "Sync Clipboard",
        onClick = { },
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
      Icon(
        imageVector = Icons.Outlined.VolumeUp,
        contentDescription = null,
        tint = VertexColors.TextSecondary
      )

      Spacer(modifier = Modifier.width(8.dp))

      Slider(
        value = volume,
        onValueChange = {
          volume = it
        },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
          thumbColor = VertexColors.AccentPrimary,
          activeTrackColor = VertexColors.AccentPrimary,
          inactiveTrackColor = VertexColors.BorderSubtle
        ),
        modifier = Modifier.weight(1f)
      )
    }

    if (showKeyboard) {
      Spacer(modifier = Modifier.height(12.dp))

      TextField(
        value = keyboardText,
        onValueChange = { newValue ->
          keyboardText = newValue
        },
        placeholder = { Text("Type to send keystrokes") },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = VertexColors.BgSurfaceHigh,
          unfocusedContainerColor = VertexColors.BgSurfaceHigh
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}