package vertexlink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vertexlink.ui.viewmodel.DiscoveryViewModel

@Composable
fun DiscoveryScreen(
  viewModel: DiscoveryViewModel,
  onDeviceSelected: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  DisposableEffect(Unit) {
    viewModel.startScanning()
    onDispose {
      viewModel.stopScanning()
    }
  }

  LazyColumn(
    modifier = modifier.fillMaxSize()
  ) {
    items(viewModel.devices) { device ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .clickable { onDeviceSelected(device.first, device.third) }
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = device.second, style = MaterialTheme.typography.titleMedium)

          Spacer(modifier = Modifier.height(4.dp))

          Text(text = device.third, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
  }
}