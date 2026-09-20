package vertexlink.controller

import com.vertexlink.network.TCPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class KeyboardController(
  private val tcpClientProvider: () -> TCPClient?,
  private val scope: CoroutineScope
) {
  fun sendKey(vkCode: Int) {
    sendCombo(listOf(vkCode))
  }

  fun sendCombo(vkCodes: List<Int>) {
    if (vkCodes.isEmpty()) return

    val client = tcpClientProvider() ?: return
    val payload = vkCodes.joinToString(",")

    scope.launch(Dispatchers.IO) {
      try {
        client.send("KEY_COMBO:$payload")
      } catch (e: IOException) {
        System.err.println("Failed to send key combo: ${e.message}")
      }
    }
  }
}