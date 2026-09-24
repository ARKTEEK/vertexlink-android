package vertexlink.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.vertexlink.ui.theme.VertexLinkTheme
import dagger.hilt.android.AndroidEntryPoint
import vertexlink.ui.screens.ControlPanel
import vertexlink.ui.screens.DiscoveryScreen
import vertexlink.ui.screens.PairingProgress
import vertexlink.ui.state.PairingUiState
import vertexlink.ui.viewmodel.DiscoveryViewModel
import vertexlink.ui.viewmodel.MacroViewModel
import vertexlink.ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val discoveryViewModel by viewModels<DiscoveryViewModel>()
  private val mainViewModel by viewModels<MainViewModel>()
  private val macroViewModel by viewModels<MacroViewModel>()

  private val notificationPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (!granted) {
        Log.w(TAG, "Notification permission denied")
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    requestNotificationPermissionIfNeeded()

    setContent {
      VertexLinkTheme {
        val targetAddress by mainViewModel.targetAddress
        val pairingState by mainViewModel.pairingState

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          when {
            targetAddress != null -> ControlPanel(
              onDisconnect = mainViewModel::disconnect,
              mouseController = mainViewModel.mouseController,
              keyboardController = mainViewModel.keyboardController,
              macros = macroViewModel.macros.value,
              onAddMacro = macroViewModel::addMacro,
              onDeleteMacro = macroViewModel::deleteMacro,
              modifier = Modifier
            )

            pairingState is PairingUiState.Connecting -> PairingProgress(
              modifier = Modifier.padding(innerPadding),
              pin = null
            )

            pairingState is PairingUiState.AwaitingConfirmation -> PairingProgress(
              modifier = Modifier.padding(innerPadding),
              pin = (pairingState as PairingUiState.AwaitingConfirmation).pin
            )

            else -> DiscoveryScreen(
              viewModel = discoveryViewModel,
              onConnect = { desktopId, address, name ->
                mainViewModel.connectToDevice(desktopId, address, name)
              },
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }

  private fun requestNotificationPermissionIfNeeded() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return
    }

    val alreadyGranted = ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    if (!alreadyGranted) {
      notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  private companion object {
    private const val TAG = "MainActivity"
  }
}