package vertexlink.ui.components.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors

enum class DeviceKind { Desktop, Phone }

@Composable
fun DeviceAvatar(
  kind: DeviceKind,
  showStatusDot: Boolean,
  isOnline: Boolean,
  modifier: Modifier = Modifier,
  size: Dp = 40.dp
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(10.dp))
      .background(VertexColors.BgSurfaceHigh),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = if (kind == DeviceKind.Desktop) {
        Icons.Outlined.Computer
      } else {
        Icons.Outlined.Smartphone
      },
      contentDescription = null,
      tint = VertexColors.AccentPrimary,
      modifier = Modifier.size(size * 0.55f)
    )

    if (showStatusDot) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(size * 0.24f)
          .clip(CircleShape)
          .background(
            if (isOnline) {
              VertexColors.Success
            } else {
              VertexColors.TextMuted
            }
          )
      )
    }
  }
}