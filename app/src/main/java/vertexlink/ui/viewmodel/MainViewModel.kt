package vertexlink.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vertexlink.network.TCPClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import vertexlink.controller.KeyboardController
import vertexlink.controller.MouseController
import vertexlink.device.DeviceInfo
import vertexlink.device.DiscoveredDevice
import vertexlink.di.IoDispatcher
import vertexlink.network.ConnectionSession
import vertexlink.network.NetworkConfig
import vertexlink.network.client.PairingClient
import vertexlink.network.client.PairingResult
import vertexlink.network.client.UDPClient
import vertexlink.network.security.CryptoUtils
import vertexlink.store.PairedDesktopStore
import vertexlink.ui.state.PairingUiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val deviceInfo: DeviceInfo,
  private val pairedDesktopStore: PairedDesktopStore,
  private val networkConfig: NetworkConfig,
  private val session: ConnectionSession,
  private val tcpClientFactory: TCPClient.Factory,
  private val udpClientFactory: UDPClient.Factory,
  private val pairingClientFactory: PairingClient.Factory,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
  val mouseController: MouseController,
  val keyboardController: KeyboardController
) : ViewModel() {

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

    viewModelScope.launch(ioDispatcher) {
      try {
        val client = tcpClientFactory.create(address, networkConfig.tcpPort)
        client.connect()

        session.attachTcpClient(client)

        val pairingClient = pairingClientFactory.create(client)
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
              session.closeTcpClient()
              _pairingState.value = PairingUiState.TimedOut
              return@launch
            }

            is PairingResult.Error -> {
              session.closeTcpClient()
              _pairingState.value = PairingUiState.Error(authResult.message)
              return@launch
            }
          }
        }

        val result = pairingClient.requestPairing(
          deviceName = deviceInfo.getDeviceName(),
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
            session.closeTcpClient()
            _pairingState.value = PairingUiState.Rejected(result.reason)
          }

          PairingResult.TimedOut -> {
            session.closeTcpClient()
            _pairingState.value = PairingUiState.TimedOut
          }

          is PairingResult.Error -> {
            session.closeTcpClient()
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
    val client = udpClientFactory.create(address, networkConfig.udpPort)

    client.setSessionKey(sessionKey)

    session.attachUdpClient(client)
  }

  fun unpair(desktopId: String) {
    pairedDesktopStore.remove(desktopId)
    _selectedDevice.value = null
  }

  fun disconnect() {
    session.closeAll()
    _targetAddress.value = null
    _connectedDeviceName.value = null
    _pairingState.value = PairingUiState.Idle
  }

  override fun onCleared() {
    super.onCleared()
    session.closeAll()
  }
}
