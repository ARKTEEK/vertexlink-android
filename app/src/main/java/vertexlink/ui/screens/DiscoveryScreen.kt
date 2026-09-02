package vertexlink.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexColors
import vertexlink.device.DiscoveredDevice
import vertexlink.ui.components.common.SectionHeader
import vertexlink.ui.components.device.DeviceInfoSheet
import vertexlink.ui.components.device.DeviceRow
import vertexlink.ui.components.device.HeaderCard
import vertexlink.ui.components.sheet.SwipeToDismissBottomSheet
import vertexlink.ui.viewmodel.DiscoveryViewModel

@Composable
fun DiscoveryScreen(
  viewModel: DiscoveryViewModel,
  onConnect: (String, String, String) -> Unit,
  onUnpair: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var isScanning by remember { mutableStateOf(true) }
  var query by remember { mutableStateOf("") }
  var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }

  DisposableEffect(isScanning) {
    if (isScanning) {
      viewModel.startScanning()
    } else {
      viewModel.stopScanning()
    }

    onDispose {
      viewModel.stopScanning()
    }
  }

  val filteredPaired = viewModel.pairedDevices.filter { it.name.contains(query, ignoreCase = true) }
  val filteredUnpaired =
    viewModel.unpairedDevices.filter { it.name.contains(query, ignoreCase = true) }

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      HeaderCard(
        deviceName = viewModel.thisDeviceName,
        isScanning = isScanning,
        onRefresh = { viewModel.startScanning() },
        onToggleScanning = { isScanning = !isScanning }
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text("Search devices") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = VertexColors.BorderFocus,
          unfocusedBorderColor = VertexColors.BorderSubtle
        ),
        modifier = Modifier.fillMaxWidth()
      )

      if (filteredPaired.isEmpty() && filteredUnpaired.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
            text = if (isScanning) "Searching for devices\u2026" else "Discovery is off",
            style = MaterialTheme.typography.bodySmall
          )
        }
      } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          if (filteredPaired.isNotEmpty()) {
            item { SectionHeader(title = "Paired") }

            items(filteredPaired, key = { it.id }) { device ->
              DeviceRow(device = device, onClick = { selectedDevice = device })
            }
          }

          if (filteredUnpaired.isNotEmpty()) {
            item { SectionHeader(title = "Available") }

            items(filteredUnpaired, key = { it.id }) { device ->
              DeviceRow(device = device, onClick = { selectedDevice = device })
            }
          }
        }
      }
    }

    selectedDevice?.let { device ->
      SwipeToDismissBottomSheet(onDismiss = { selectedDevice = null }) {
        DeviceInfoSheet(
          device = device,
          onConnect = {
            onConnect(device.id, device.address, device.name)
            selectedDevice = null
          },
          onUnpair = {
            onUnpair(device.id)
            selectedDevice = null
          }
        )
      }
    }
  }
}