package vertexlink.network.client

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class UDPClient(serverAddressString: String, private val port: Int) {
  private val serverAddress: InetAddress = InetAddress.getByName(serverAddressString)
  private val socket: DatagramSocket = DatagramSocket()
  private var isSending = false

  fun send(payload: ByteArray) {
    try {
      val packet = DatagramPacket(payload, payload.size, serverAddress, port)
      socket.send(packet)
    } catch (e: Exception) {

      System.err.println("Failed to send UDP packet: ${e.message}")
    }
  }

  fun receive(timeoutMs: Int): ByteArray? = try {
    socket.soTimeout = timeoutMs

    val buffer = ByteArray(2048)
    val packet = DatagramPacket(buffer, buffer.size)

    socket.receive(packet)

    packet.data.copyOf(packet.length)
  } catch (e: SocketTimeoutException) {
    null
  } catch (e: Exception) {
    System.err.println("Failed to receive UDP packet: ${e.message}")
    null
  }

  fun stopPeriodicSending() {
    isSending = false
  }

  fun close() {
    stopPeriodicSending()
    if (!socket.isClosed) {
      socket.close()
    }
  }
}