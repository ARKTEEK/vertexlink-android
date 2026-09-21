package vertexlink.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import vertexlink.di.ApplicationScope
import vertexlink.di.IoDispatcher
import vertexlink.network.ConnectionSession
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MouseController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
    val client = session.tcpClient ?: return

    scope.launch(ioDispatcher) {
      try {
        client.send(command)
      } catch (e: IOException) {
        System.err.println("Failed to send command: ${e.message}")
      }
    }
  }

  private fun sendUdpCommand(command: String) {
    val client = session.udpClient ?: return

    scope.launch(ioDispatcher) {
      client.send(command)
    }
  }
}
