package vertexlink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    CircularProgressIndicator()
    Spacer(modifier = Modifier.height(16.dp))

    if (pin != null) {
      Text("Verify PIN on Desktop:")
      Spacer(modifier = Modifier.height(8.dp))

      Text(text = pin, style = MaterialTheme.typography.headlineMedium)
      Spacer(modifier = Modifier.height(8.dp))

      Text("Waiting for Desktop approval…", style = MaterialTheme.typography.bodyMedium)
    } else {
      Text("Connecting…")
    }
  }
}