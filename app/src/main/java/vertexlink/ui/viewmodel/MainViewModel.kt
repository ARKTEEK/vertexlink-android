package vertexlink.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vertexlink.network.TCPClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vertexlink.device.DeviceIdentity
import vertexlink.device.DeviceInfo
import vertexlink.network.client.PairingClient
import vertexlink.network.client.PairingResult
import vertexlink.ui.state.PairingUiState

private const val DESKTOP_PORT = 28401

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val identity = DeviceIdentity(application.applicationContext)
  private val deviceInfo = DeviceInfo()

  var tcpClient: TCPClient? = null
    private set

  private val _targetAddress = mutableStateOf<String?>(null)
  val targetAddress: State<String?> = _targetAddress

  private val _pairingState = mutableStateOf<PairingUiState>(PairingUiState.Idle)
  val pairingState: State<PairingUiState> = _pairingState

  fun pairWithDevice(address: String) {
    _pairingState.value = PairingUiState.Connecting

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val client = TCPClient(address, DESKTOP_PORT)
        client.connect()

        tcpClient = client

        val result = PairingClient(client, identity).requestPairing(
          deviceName = deviceInfo.getDeviceName(getApplication()),
          onPinGenerated = { pin ->
            _pairingState.value = PairingUiState.AwaitingConfirmation(pin, address)
          }
        )

        when (result) {
          is PairingResult.Accepted -> {
            _targetAddress.value = address
          }

          is PairingResult.Rejected -> {
            client.close()

            tcpClient = null
            _pairingState.value = PairingUiState.Rejected(result.reason)
          }

          PairingResult.TimedOut -> {
            client.close()

            tcpClient = null
            _pairingState.value = PairingUiState.TimedOut
          }

          is PairingResult.Error -> {
            client.close()

            tcpClient = null
            _pairingState.value = PairingUiState.Error(result.message)
          }
        }
      } catch (e: Exception) {
        System.err.println("Could not connect: ${e.message}")
        _pairingState.value = PairingUiState.Error(e.message ?: "Connection failed")
      }
    }
  }

  fun sendMessage(message: String) {
    if (message.isNotEmpty()) {
      viewModelScope.launch(Dispatchers.IO) {
        try {
          tcpClient?.send(message)
        } catch (e: Exception) {
          System.err.println("Failed to send: ${e.message}")
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    tcpClient?.close()
  }
}