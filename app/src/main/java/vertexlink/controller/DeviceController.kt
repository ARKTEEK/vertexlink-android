package vertexlink.controller

import com.vertexlink.network.TCPClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import vertexlink.device.DeviceInfo
import vertexlink.di.IoDispatcher
import vertexlink.network.ConnectionSession
import vertexlink.network.NetworkConfig
import vertexlink.network.client.PairingClient
import vertexlink.network.client.PairingResult
import vertexlink.network.client.UDPClient
import vertexlink.network.security.CryptoUtils
import vertexlink.store.PairedDesktopStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceController @Inject constructor(
  private val deviceInfo: DeviceInfo,
  private val pairedDesktopStore: PairedDesktopStore,
  private val networkConfig: NetworkConfig,
  private val session: ConnectionSession,
  private val tcpClientFactory: TCPClient.Factory,
  private val udpClientFactory: UDPClient.Factory,
  private val pairingClientFactory: PairingClient.Factory,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

  suspend fun connectToDevice(
    desktopId: String,
    address: String,
    onPinGenerated: (String) -> Unit
  ): PairingResult = withContext(ioDispatcher) {
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

            return@withContext authResult
          }

          is PairingResult.Rejected -> {
            pairedDesktopStore.remove(desktopId)
          }

          else -> {
            session.closeTcpClient()

            return@withContext authResult
          }
        }
      }

      val result = pairingClient.requestPairing(
        deviceName = deviceInfo.getDeviceName(),
        onPinGenerated = onPinGenerated
      )

      when (result) {
        is PairingResult.Accepted -> {
          pairedDesktopStore.save(result.desktopId, result.desktopName, result.token)

          val sessionKey = CryptoUtils.deriveKeyFromToken(result.token)

          openUdpChannel(address, sessionKey)
        }

        else -> {
          session.closeTcpClient()
        }
      }

      result
    } catch (e: Exception) {
      System.err.println("Could not connect: ${e.message}")

      PairingResult.Error(e.message ?: "Connection failed")
    }
  }

  private fun openUdpChannel(address: String, sessionKey: ByteArray) {
    val client = udpClientFactory.create(address, networkConfig.udpPort)
    client.setSessionKey(sessionKey)

    session.attachUdpClient(client)
  }

  fun disconnect() {
    session.closeAll()
  }
}