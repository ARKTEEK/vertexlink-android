package vertexlink.ui.components.controlpanel.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun VolumePopup(
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