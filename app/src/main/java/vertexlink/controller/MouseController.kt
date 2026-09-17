package vertexlink.controller

import com.vertexlink.network.TCPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vertexlink.network.client.UDPClient
import java.io.IOException

class MouseController(
  private val tcpClientProvider: () -> TCPClient?,
  private val udpClientProvider: () -> UDPClient?,
  private val scope: CoroutineScope
) {
  fun sendMouseMove(dx: Int, dy: Int) {
    sendUdpCommand("MOUSE_MOVE:$dx,$dy")
  }

  fun sendLeftClick() {
    sendTcpCommand("MOUSE_LEFT_CLICK")
  }

  fun sendRightClick() {
    sendTcpCommand("MOUSE_RIGHT_CLICK")
  }

  fun sendLeftDown() {
    sendTcpCommand("MOUSE_LEFT_DOWN")
  }

  fun sendLeftUp() {
    sendTcpCommand("MOUSE_LEFT_UP")
  }

  private fun sendTcpCommand(command: String) {
    val client = tcpClientProvider() ?: return

    scope.launch(Dispatchers.IO) {
      try {
        client.send(command)
      } catch (e: IOException) {
        System.err.println("Failed to send command: ${e.message}")
      }
    }
  }

  private fun sendUdpCommand(command: String) {
    val client = udpClientProvider() ?: return

    scope.launch(Dispatchers.IO) {
      client.send(command.toByteArray())
    }
  }
}