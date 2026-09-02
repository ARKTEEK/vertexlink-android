package vertexlink.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import vertexlink.device.DeviceIdentity
import vertexlink.device.DeviceInfo
import vertexlink.device.DiscoveredDevice
import vertexlink.network.mdns.DeviceBroadcaster
import vertexlink.network.mdns.DeviceScanner
import vertexlink.store.PairedDesktopStore

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {
  private val identity = DeviceIdentity(application)
  private val deviceId = identity.getId()
  private val pairedDesktopStore = PairedDesktopStore(application)
  private val deviceInfo = DeviceInfo()

  val thisDeviceName: String = deviceInfo.getDeviceName(application)

  val pairedDevices = mutableStateListOf<DiscoveredDevice>()
  val unpairedDevices = mutableStateListOf<DiscoveredDevice>()

  private val scanner = DeviceScanner(application, deviceId) { id, name, address ->
    onDeviceFound(id, name, address)
  }

  private val broadcaster = DeviceBroadcaster(application, deviceId)

  fun startScanning() {
    loadPairedDevices()
    unpairedDevices.clear()
    broadcaster.start(deviceInfo.getDeviceName(getApplication()), 28401)
    scanner.start()
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

    val unpairedIndex = unpairedDevices.indexOfFirst { it.id == id }
    val device = DiscoveredDevice(id, name, address, isPaired = false, isOnline = true)

    if (unpairedIndex != -1) {
      unpairedDevices[unpairedIndex] = device
    } else {
      unpairedDevices.add(device)
    }
  }
}