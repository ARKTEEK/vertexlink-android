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
class KeyboardController @Inject constructor(
  private val session: ConnectionSession,
  @ApplicationScope private val scope: CoroutineScope,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
  fun sendKey(vkCode: Int) {
    sendCombo(listOf(vkCode))
  }

  fun sendCombo(vkCodes: List<Int>) {
    if (vkCodes.isEmpty()) return

    val client = session.tcpClient ?: return
    val payload = vkCodes.joinToString(",")

    scope.launch(ioDispatcher) {
      try {
        client.send("KEY_COMBO:$payload")
      } catch (e: IOException) {
        System.err.println("Failed to send key combo: ${e.message}")
      }
    }
  }
}
