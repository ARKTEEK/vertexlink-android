package vertexlink.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.padding(top = 14.dp, bottom = 6.dp, start = 4.dp, end = 4.dp)
  ) {
    Text(text = title.uppercase(), style = MaterialTheme.typography.titleSmall)

    HorizontalDivider(
      modifier = Modifier
        .weight(1f)
        .padding(start = 8.dp),
      thickness = DividerDefaults.Thickness,
      color = VertexColors.BorderSubtle
    )
  }
}