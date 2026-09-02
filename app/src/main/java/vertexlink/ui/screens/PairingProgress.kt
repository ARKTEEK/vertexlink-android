package vertexlink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertexlink.ui.theme.JetBrainsMono
import com.vertexlink.ui.theme.VertexColors

@Composable
fun PairingProgress(
  pin: String?,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    CircularProgressIndicator(color = VertexColors.AccentPrimary)

    Spacer(modifier = Modifier.height(20.dp))

    if (pin != null) {
      Text("Verify PIN on Desktop", style = MaterialTheme.typography.bodyMedium)

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = pin,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = JetBrainsMono,
          fontSize = 28.sp
        ),
        modifier = Modifier
          .background(VertexColors.BgSurfaceHigh, RoundedCornerShape(10.dp))
          .padding(horizontal = 20.dp, vertical = 10.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text("Waiting for Desktop approval\u2026", style = MaterialTheme.typography.bodySmall)
    } else {
      Text("Connecting\u2026", style = MaterialTheme.typography.bodyMedium)
    }
  }
}