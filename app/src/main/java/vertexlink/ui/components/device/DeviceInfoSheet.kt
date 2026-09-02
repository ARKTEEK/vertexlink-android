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
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.MonoLabelStyle
import com.vertexlink.ui.theme.VertexColors
import vertexlink.device.DiscoveredDevice
import vertexlink.ui.components.common.StatusPill

@Composable
fun DeviceInfoSheet(
  device: DiscoveredDevice,
  onConnect: () -> Unit,
  onUnpair: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(horizontal = 20.dp)) {
    Text(text = "Device Info", style = MaterialTheme.typography.titleLarge)

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(VertexColors.BgSurfaceMid)
        .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      DeviceAvatar(kind = DeviceKind.Phone, showStatusDot = false, isOnline = device.isOnline)

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(text = device.name, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(6.dp))

        Row {
          if (device.isPaired) {
            StatusPill(text = "Paired", isPositive = true)
            Spacer(modifier = Modifier.width(6.dp))
          }

          StatusPill(
            text = if (device.isOnline) {
              "Online"
            } else {
              "Offline"
            },
            isPositive = device.isOnline
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(text = "CONNECTION DETAILS", style = MaterialTheme.typography.titleSmall)

    Spacer(modifier = Modifier.height(8.dp))

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(VertexColors.BgSurfaceMid)
        .border(1.dp, VertexColors.BorderSubtle, RoundedCornerShape(14.dp))
    ) {
      InfoDetailRow(
        label = "Client ID",
        value = device.id,
        onCopy = { }
      )

      InfoDetailRow(
        label = "IPv4 Address",
        value = device.address.ifEmpty { "\u2014" },
        onCopy = { }
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (device.isOnline) {
      Button(
        onClick = onConnect,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          containerColor = VertexColors.AccentPrimary,
          contentColor = VertexColors.TextOnAccent
        )
      ) {
        Text(
          if (device.isPaired) {
            "Connect"
          } else {
            "Pair & Connect"
          }
        )
      }
    }

    if (device.isPaired) {
      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onUnpair,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = VertexColors.Danger)
      ) {
        Text("Unpair")
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun InfoDetailRow(label: String, value: String, onCopy: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 4.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, style = MonoLabelStyle)
    }

    IconButton(onClick = onCopy) {
      Icon(
        imageVector = Icons.Outlined.ContentCopy,
        contentDescription = "Copy",
        tint = VertexColors.TextSecondary
      )
    }
  }
}