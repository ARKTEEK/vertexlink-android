package vertexlink.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun IconRoundButton(
  icon: ImageVector,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  active: Boolean = false
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(40.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(
        if (active) {
          VertexColors.AccentPrimary
        } else {
          VertexColors.BgSurfaceHigh
        }
      )
      .border(
        1.dp,
        if (active) {
          Color.Transparent
        } else {
          VertexColors.BorderSubtle
        },
        RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = if (active) {
        VertexColors.TextOnAccent
      } else {
        VertexColors.TextSecondary
      },
      modifier = Modifier.size(22.dp)
    )
  }
}