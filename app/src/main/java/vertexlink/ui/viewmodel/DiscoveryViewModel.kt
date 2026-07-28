package vertexlink.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import vertexlink.device.DeviceIdentity
import vertexlink.device.DeviceInfo
import vertexlink.network.mdns.DeviceBroadcaster
import vertexlink.network.mdns.DeviceScanner

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {
  private val identity = DeviceIdentity(application)
  private val deviceId = identity.getId()

  val devices = mutableStateListOf<Pair<String, String>>()

  private val scanner = DeviceScanner(application, deviceId) { name, address ->
    val device = Pair(name, address)

    if (!devices.contains(device)) {
      devices.add(device)
    }
  }

  private val broadcaster = DeviceBroadcaster(application, deviceId)
  private val deviceInfo = DeviceInfo()

  fun startScanning() {
    devices.clear()
    broadcaster.start(deviceInfo.getDeviceName(getApplication()), 28401)
    scanner.start()
  }

  fun stopScanning() {
    scanner.stop()
    broadcaster.stop()
  }
}