package vertexlink.network

import com.vertexlink.network.TCPClient
import vertexlink.network.client.UDPClient
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ConnectionSession @Inject constructor() {
  @Volatile
  var tcpClient: TCPClient? = null
    private set

  @Volatile
  var udpClient: UDPClient? = null
    private set

  fun attachTcpClient(client: TCPClient) {
    tcpClient = client
  }

  fun closeTcpClient() {
    tcpClient?.close()
    tcpClient = null
  }

  fun attachUdpClient(client: UDPClient) {
    udpClient?.close()
    udpClient = client
  }

  fun closeAll() {
    closeTcpClient()

    udpClient?.close()
    udpClient = null
  }
}
