package vertexlink.ui.components.device

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import vertexlink.device.DiscoveredDevice
import vertexlink.ui.components.common.StatusPill

@Composable
fun DeviceRow(
  device: DiscoveredDevice,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
      .padding(10.dp)
  ) {
    DeviceAvatar(
      kind = DeviceKind.Phone,
      showStatusDot = false,
      isOnline = device.isOnline,
      size = 36.dp
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(text = device.name, style = MaterialTheme.typography.bodyMedium)

      if (device.isPaired) {
        Spacer(modifier = Modifier.height(4.dp))
        StatusPill(
          text = if (device.isOnline) "Online" else "Offline",
          isPositive = device.isOnline
        )
      }
    }
  }
}