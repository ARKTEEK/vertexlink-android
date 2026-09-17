package vertexlink.network.client

import vertexlink.network.security.UDPCrypto
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UDPClient(serverAddressString: String, private val port: Int) {
  private val serverAddress: InetAddress = InetAddress.getByName(serverAddressString)
  private val socket: DatagramSocket = DatagramSocket()
  private var crypto: UDPCrypto? = null

  fun setSessionKey(keyBytes: ByteArray) {
    this.crypto = UDPCrypto(keyBytes)
  }

  fun send(command: String) {
    val cryptoInstance = crypto

    if (cryptoInstance == null) {
      return
    }

    try {
      val plainBytes = command.toByteArray(Charsets.UTF_8)
      val encryptedBytes = cryptoInstance.encrypt(plainBytes)
      val packet = DatagramPacket(encryptedBytes, encryptedBytes.size, serverAddress, port)

      socket.send(packet)
    } catch (e: Exception) {
      System.err.println("Failed to send UDP packet: ${e.message}")
    }
  }

  fun close() {
    if (!socket.isClosed) {
      socket.close()
    }
  }
}