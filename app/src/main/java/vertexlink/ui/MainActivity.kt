package vertexlink.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vertexlink.ui.theme.VertexLinkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vertexlink.network.UDPClient
import vertexlink.ui.screens.discovery.DiscoveryScreen
import vertexlink.ui.screens.discovery.DiscoveryViewModel

class MainActivity : ComponentActivity() {
  private var udpClient: UDPClient? = null
  private val discoveryViewModel by viewModels<DiscoveryViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      VertexLinkTheme {
        var targetAddress by remember { mutableStateOf<String?>(null) }
        val currentAddress = targetAddress

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          if (currentAddress == null) {
            DiscoveryScreen(
              viewModel = discoveryViewModel,
              onDeviceSelected = { address ->
                CoroutineScope(Dispatchers.IO).launch {
                  try {
                    udpClient = UDPClient(address, 28401)
                    targetAddress = address
                  } catch (e: Exception) {
                    System.err.println("Could not initialize UDP background thread: ${e.message}")
                  }
                }
              },
              modifier = Modifier.padding(innerPadding)
            )
          } else {
            ControlPanel(
              udpClient = udpClient,
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    udpClient?.close()
  }

  @Composable
  fun ControlPanel(udpClient: UDPClient?, modifier: Modifier = Modifier.Companion) {
    var textMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      TextField(
        value = textMessage,
        onValueChange = { textMessage = it },
        label = { Text("Message to Desktop") },
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          val currentMessage = textMessage

          if (currentMessage.isNotEmpty()) {
            scope.launch(Dispatchers.IO) {
              udpClient?.send(currentMessage.toByteArray())
            }
          }
        },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Send Packet")
      }
    }
  }
}