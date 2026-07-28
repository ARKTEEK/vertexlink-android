package vertexlink.network.mdns

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager

class DeviceScanner(
  context: Context,
  private val deviceId: String,
  private val onDeviceDiscovered: (String, String) -> Unit
) {
  private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
  private val wifiManager =
    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
  private var multicastLock: WifiManager.MulticastLock? = null
  private val serviceType = "_vertexlink._tcp"
  private var listener: NsdManager.DiscoveryListener? = null

  fun start() {
    if (listener != null) {
      return
    }

    multicastLock = wifiManager.createMulticastLock("VertexLinkMulticastLock").apply {
      setReferenceCounted(true)
      acquire()
    }

    listener = object : NsdManager.DiscoveryListener {
      override fun onDiscoveryStarted(serviceType: String) {}
      override fun onDiscoveryStopped(serviceType: String) {}

      override fun onServiceFound(service: NsdServiceInfo) {
        if (!service.serviceType.contains(serviceType)) {
          return
        }

        nsdManager.resolveService(
          service,
          object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
              val remoteId = serviceInfo.attributes["device_id"]?.decodeToString()

              if (deviceId == remoteId) {
                return
              }

              if (remoteId == null) {
                return
              }

              val address = serviceInfo.host?.hostAddress ?: return

              onDeviceDiscovered(
                serviceInfo.serviceName,
                address
              )
            }
          }
        )
      }

      override fun onServiceLost(service: NsdServiceInfo) {}
      override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        stop()
      }

      override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        stop()
      }
    }

    nsdManager.discoverServices(
      serviceType,
      NsdManager.PROTOCOL_DNS_SD,
      listener!!
    )
  }

  fun stop() {
    if (listener == null) {
      return
    }

    nsdManager.stopServiceDiscovery(listener)
    listener = null

    if (multicastLock != null) {
      multicastLock?.release()
      multicastLock = null
    }
  }
}