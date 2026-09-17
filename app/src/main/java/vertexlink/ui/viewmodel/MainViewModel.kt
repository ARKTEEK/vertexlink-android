package vertexlink.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vertexlink.network.TCPClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vertexlink.controller.MouseController
import vertexlink.device.DeviceIdentity
import vertexlink.device.DeviceInfo
import vertexlink.device.DiscoveredDevice
import vertexlink.network.client.PairingClient
import vertexlink.network.client.PairingResult
import vertexlink.network.client.UDPClient
import vertexlink.network.security.CryptoUtils
import vertexlink.store.PairedDesktopStore
import vertexlink.ui.state.PairingUiState

private const val DESKTOP_PORT = 28401
private const val DESKTOP_UDP_PORT = 28402

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val identity = DeviceIdentity(application.applicationContext)
  private val deviceInfo = DeviceInfo()
  private val pairedDesktopStore = PairedDesktopStore(application.applicationContext)

  var tcpClient: TCPClient? = null
    private set

  var udpClient: UDPClient? = null
    private set

  val mouseController = MouseController(
    scope = viewModelScope,
    tcpClientProvider = { tcpClient },
    udpClientProvider = { udpClient }
  )

  private val _targetAddress = mutableStateOf<String?>(null)
  val targetAddress: State<String?> = _targetAddress

  private val _connectedDeviceName = mutableStateOf<String?>(null)
  val connectedDeviceName: State<String?> = _connectedDeviceName

  private val _pairingState = mutableStateOf<PairingUiState>(PairingUiState.Idle)
  val pairingState: State<PairingUiState> = _pairingState

  private val _selectedDevice = mutableStateOf<DiscoveredDevice?>(null)
  val selectedDevice: State<DiscoveredDevice?> = _selectedDevice

  fun selectDevice(device: DiscoveredDevice?) {
    _selectedDevice.value = device
  }

  fun connectToDevice(desktopId: String, address: String, name: String) {
    _pairingState.value = PairingUiState.Connecting
    _selectedDevice.value = null

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val client = TCPClient(address, DESKTOP_PORT)
        client.connect()

        tcpClient = client

        val pairingClient = PairingClient(client, identity)
        val stored = pairedDesktopStore.find(desktopId)

        if (stored != null) {
          val (_, token) = stored

          when (val authResult = pairingClient.authenticate(desktopId, token)) {
            is PairingResult.Accepted -> {
              val sessionKey = CryptoUtils.deriveKeyFromToken(token)
              openUdpChannel(address, sessionKey)

              _connectedDeviceName.value = name
              _targetAddress.value = address
              return@launch
            }

            is PairingResult.Rejected -> {
              pairedDesktopStore.remove(desktopId)
            }

            PairingResult.TimedOut -> {
              client.close()
              tcpClient = null
              _pairingState.value = PairingUiState.TimedOut
              return@launch
            }

            is PairingResult.Error -> {
              client.close()
              tcpClient = null
              _pairingState.value = PairingUiState.Error(authResult.message)
              return@launch
            }
          }
        }

        val result = pairingClient.requestPairing(
          deviceName = deviceInfo.getDeviceName(getApplication()),
          onPinGenerated = { pin ->
            _pairingState.value = PairingUiState.AwaitingConfirmation(pin, address)
          }
        )

        when (result) {
          is PairingResult.Accepted -> {
            pairedDesktopStore.save(result.desktopId, result.desktopName, result.token)
            val sessionKey = CryptoUtils.deriveKeyFromToken(result.token)
            openUdpChannel(address, sessionKey)
            _connectedDeviceName.value = result.desktopName
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

  private fun openUdpChannel(address: String, sessionKey: ByteArray) {
    udpClient?.close()

    val client = UDPClient(address, DESKTOP_UDP_PORT)
    
    client.setSessionKey(sessionKey)

    udpClient = client
  }

  fun unpair(desktopId: String) {
    pairedDesktopStore.remove(desktopId)
    _selectedDevice.value = null
  }

  fun disconnect() {
    tcpClient?.close()
    tcpClient = null
    udpClient?.close()
    udpClient = null
    _targetAddress.value = null
    _connectedDeviceName.value = null
    _pairingState.value = PairingUiState.Idle
  }

  override fun onCleared() {
    super.onCleared()
    tcpClient?.close()
    udpClient?.close()
  }
}