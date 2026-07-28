package vertexlink.network.mdns

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

class DeviceBroadcaster(context: Context, private val deviceId: String) {

  private val nsdManager =
    context.getSystemService(Context.NSD_SERVICE) as NsdManager

  private val serviceType = "_vertexlink._tcp."

  private var registrationListener:
      NsdManager.RegistrationListener? = null

  private var isRegistered = false

  fun start(deviceName: String, port: Int) {
    if (registrationListener != null) {
      return
    }


    val serviceInfo = NsdServiceInfo().apply {
      serviceName = deviceName
      serviceType = this@DeviceBroadcaster.serviceType
      this.port = port
      this.setAttribute("device_id", deviceId)
    }

    registrationListener =
      object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(
          nsdServiceInfo: NsdServiceInfo
        ) {
          isRegistered = true
        }

        override fun onRegistrationFailed(
          nsdServiceInfo: NsdServiceInfo,
          errorCode: Int
        ) {
          isRegistered = false
          registrationListener = null
        }

        override fun onServiceUnregistered(
          nsdServiceInfo: NsdServiceInfo
        ) {
          isRegistered = false
          registrationListener = null
        }

        override fun onUnregistrationFailed(
          nsdServiceInfo: NsdServiceInfo,
          errorCode: Int
        ) {
          isRegistered = false
          registrationListener = null
        }
      }

    nsdManager.registerService(
      serviceInfo,
      NsdManager.PROTOCOL_DNS_SD,
      registrationListener!!
    )
  }

  fun stop() {
    val listener = registrationListener ?: return

    try {
      nsdManager.unregisterService(listener)
    } catch (_: IllegalArgumentException) {
    }

    isRegistered = false
    registrationListener = null
  }
}