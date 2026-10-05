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

enum class IconButtonTone { Primary, Secondary, Danger }

@Composable
fun IconRoundButton(
  icon: ImageVector,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  active: Boolean = false,
  tone: IconButtonTone = IconButtonTone.Primary,
  tinted: Boolean = false
) {
  val fill = when {
    !active && tinted && tone == IconButtonTone.Danger -> VertexColors.DangerSurface
    !active -> VertexColors.Card
    tone == IconButtonTone.Secondary -> VertexColors.AccentSecondary
    tone == IconButtonTone.Danger -> VertexColors.Danger
    else -> VertexColors.AccentPrimary
  }

  val border = when {
    active -> Color.Transparent
    tinted && tone == IconButtonTone.Danger -> VertexColors.DangerBorder
    else -> VertexColors.BorderSubtle
  }

  val tint = when {
    active -> VertexColors.TextOnAccent
    tone == IconButtonTone.Danger -> VertexColors.Danger
    tone == IconButtonTone.Secondary -> VertexColors.TealDeep
    else -> VertexColors.Magenta
  }

  val shape = RoundedCornerShape(12.dp)

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(40.dp)
      .clip(shape)
      .background(fill)
      .border(1.dp, border, shape)
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = tint,
      modifier = Modifier.size(22.dp)
    )
  }
}
