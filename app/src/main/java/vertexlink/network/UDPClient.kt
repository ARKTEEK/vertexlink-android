package vertexlink.network

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

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