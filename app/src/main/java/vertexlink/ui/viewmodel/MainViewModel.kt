package vertexlink.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import vertexlink.controller.DeviceController
import vertexlink.controller.KeyboardController
import vertexlink.controller.MouseController
import vertexlink.device.DiscoveredDevice
import vertexlink.network.client.PairingResult
import vertexlink.ui.state.PairingUiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val deviceController: DeviceController,
  val mouseController: MouseController,
  val keyboardController: KeyboardController
) : ViewModel() {

  private val _targetAddress = mutableStateOf<String?>(null)
  val targetAddress: State<String?> = _targetAddress

  private val _connectedDeviceName = mutableStateOf<String?>(null)

  private val _pairingState = mutableStateOf<PairingUiState>(PairingUiState.Idle)
  val pairingState: State<PairingUiState> = _pairingState

  private val _selectedDevice = mutableStateOf<DiscoveredDevice?>(null)

  fun connectToDevice(desktopId: String, address: String, name: String) {
    _pairingState.value = PairingUiState.Connecting
    _selectedDevice.value = null

    viewModelScope.launch {
      val result = deviceController.connectToDevice(
        desktopId = desktopId,
        address = address,
        onPinGenerated = { pin ->
          _pairingState.value = PairingUiState.AwaitingConfirmation(pin, address)
        }
      )

      when (result) {
        is PairingResult.Accepted -> {
          _connectedDeviceName.value = result.desktopName
          _targetAddress.value = address
          _pairingState.value = PairingUiState.Idle
        }

        is PairingResult.Rejected -> {
          _pairingState.value = PairingUiState.Rejected(result.reason)
        }

        PairingResult.TimedOut -> {
          _pairingState.value = PairingUiState.TimedOut
        }

        is PairingResult.Error -> {
          _pairingState.value = PairingUiState.Error(result.message)
        }
      }
    }
  }

  fun disconnect() {
    deviceController.disconnect()

    _targetAddress.value = null
    _connectedDeviceName.value = null
    _pairingState.value = PairingUiState.Idle
  }

  override fun onCleared() {
    super.onCleared()
    deviceController.disconnect()
  }
}