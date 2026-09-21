package vertexlink.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vertexlink.device.DeviceInfo
import vertexlink.device.DiscoveredDevice
import vertexlink.network.NetworkConfig
import vertexlink.network.mdns.DeviceBroadcaster
import vertexlink.network.mdns.DeviceScanner
import vertexlink.store.PairedDesktopStore
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
  private val pairedDesktopStore: PairedDesktopStore,
  private val deviceInfo: DeviceInfo,
  private val networkConfig: NetworkConfig,
  private val scanner: DeviceScanner,
  private val broadcaster: DeviceBroadcaster
) : ViewModel() {
  val thisDeviceName: String = deviceInfo.getDeviceName()

  val pairedDevices = mutableStateListOf<DiscoveredDevice>()
  val unpairedDevices = mutableStateListOf<DiscoveredDevice>()

  fun startScanning() {
    loadPairedDevices()
    unpairedDevices.clear()
    broadcaster.start(deviceInfo.getDeviceName(), networkConfig.tcpPort)
    scanner.start { id, name, address -> onDeviceFound(id, name, address) }
  }

  fun stopScanning() {
    scanner.stop()
    broadcaster.stop()
  }

  private fun loadPairedDevices() {
    pairedDevices.clear()

    pairedDesktopStore.getAll().forEach { (id, name, _) ->
      pairedDevices.add(DiscoveredDevice(id, name, "", isPaired = true, isOnline = false))
    }
  }

  fun unpair(id: String) {
    pairedDesktopStore.remove(id)

    val pairedIndex = pairedDevices.indexOfFirst { it.id == id }

    if (pairedIndex == -1) {
      return
    }

    val device = pairedDevices.removeAt(pairedIndex)
    
    if (device.isOnline) {
      upsertUnpaired(device.copy(isPaired = false))
    }
  }

  private fun onDeviceFound(id: String, name: String, address: String) {
    val pairedIndex = pairedDevices.indexOfFirst { it.id == id }

    if (pairedIndex != -1) {
      pairedDevices[pairedIndex] = pairedDevices[pairedIndex].copy(
        name = name,
        address = address,
        isOnline = true
      )

      return
    }

    upsertUnpaired(DiscoveredDevice(id, name, address, isPaired = false, isOnline = true))
  }

  private fun upsertUnpaired(device: DiscoveredDevice) {
    val unpairedIndex = unpairedDevices.indexOfFirst { it.id == device.id }

    if (unpairedIndex != -1) {
      unpairedDevices[unpairedIndex] = device
    } else {
      unpairedDevices.add(device)
    }
  }
}
