package vertexlink.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun ActionChip(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  active: Boolean = false
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
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
          VertexColors.AccentPrimary
        } else {
          VertexColors.BorderSubtle
        },
        RoundedCornerShape(10.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (active) {
        VertexColors.TextOnAccent
      } else {
        VertexColors.AccentPrimary
      }
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = if (active) {
        VertexColors.TextOnAccent
      } else {
        VertexColors.TextPrimary
      }
    )
  }
}