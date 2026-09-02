package vertexlink.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.ui.components.common.IconRoundButton
import vertexlink.ui.components.common.StatusPill

@Composable
fun HeaderCard(
  deviceName: String,
  isScanning: Boolean,
  onRefresh: () -> Unit,
  onToggleScanning: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(VertexColors.BgSurfaceMid)
      .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(14.dp))
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    DeviceAvatar(kind = DeviceKind.Phone, showStatusDot = false, isOnline = true)

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(text = deviceName, style = MaterialTheme.typography.titleMedium)

      Spacer(modifier = Modifier.height(4.dp))

      StatusPill(
        text = if (isScanning) {
          "Discoverable"
        } else {
          "Hidden"
        }, isPositive = isScanning
      )
    }

    IconRoundButton(
      icon = Icons.Outlined.Refresh,
      contentDescription = "Rescan",
      onClick = onRefresh
    )

    Spacer(modifier = Modifier.width(8.dp))

    IconRoundButton(
      icon = Icons.Outlined.PowerSettingsNew,
      contentDescription = "Toggle discoverability",
      onClick = onToggleScanning,
      active = isScanning
    )
  }
}