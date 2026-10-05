package vertexlink.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun CaptionStrip(
  title: String,
  modifier: Modifier = Modifier,
  horizontalPadding: Dp = 14.dp
) {
  Text(
    text = title.uppercase(),
    style = MaterialTheme.typography.titleSmall.copy(
      color = VertexColors.TextSecondary,
      fontSize = 11.sp
    ),
    modifier = modifier
      .fillMaxWidth()
      .background(VertexColors.CaptionBar)
      .padding(horizontal = horizontalPadding, vertical = 8.dp)
  )
}
