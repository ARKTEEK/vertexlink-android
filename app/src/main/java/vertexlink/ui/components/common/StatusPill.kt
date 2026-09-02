package vertexlink.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun StatusPill(
  text: String,
  isPositive: Boolean,
  modifier: Modifier = Modifier
) {
  val background = if (isPositive) VertexColors.SuccessSurface else VertexColors.MutedSurface
  val textColor = if (isPositive) VertexColors.Success else VertexColors.TextMuted

  Text(
    text = text,
    color = textColor,
    fontSize = 10.5.sp,
    modifier = modifier
      .background(background, RoundedCornerShape(999.dp))
      .padding(horizontal = 8.dp, vertical = 2.dp)
  )
}