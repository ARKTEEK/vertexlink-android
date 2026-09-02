package vertexlink.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.vertexlink.ui.theme.VertexLinkTheme
import vertexlink.ui.screens.ControlPanel
import vertexlink.ui.screens.DiscoveryScreen
import vertexlink.ui.screens.PairingProgress
import vertexlink.ui.state.PairingUiState
import vertexlink.ui.viewmodel.DiscoveryViewModel
import vertexlink.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  private val discoveryViewModel by viewModels<DiscoveryViewModel>()
  private val mainViewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    setContent {
      VertexLinkTheme {
        val targetAddress by mainViewModel.targetAddress
        val connectedDeviceName by mainViewModel.connectedDeviceName
        val pairingState by mainViewModel.pairingState

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          when {
            targetAddress != null -> ControlPanel(
              deviceName = connectedDeviceName ?: "Desktop",
              onDisconnect = mainViewModel::disconnect,
              modifier = Modifier.padding(innerPadding)
            )

            pairingState is PairingUiState.Connecting -> PairingProgress(
              modifier = Modifier.padding(innerPadding),
              pin = null
            )

            pairingState is PairingUiState.AwaitingConfirmation -> PairingProgress(
              modifier = Modifier.padding(innerPadding),
              pin = (pairingState as PairingUiState.AwaitingConfirmation).pin,
            )

            else -> DiscoveryScreen(
              viewModel = discoveryViewModel,
              onConnect = { desktopId, address, name ->
                mainViewModel.connectToDevice(desktopId, address, name)
              },
              onUnpair = mainViewModel::unpair,
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }
}