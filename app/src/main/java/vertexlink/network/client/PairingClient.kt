package vertexlink.network.client

import com.vertexlink.network.TCPClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vertexlink.device.DeviceIdentity
import vertexlink.network.security.CryptoUtils
import vertexlink.protocol.Protocol

import java.io.IOException
import java.net.SocketTimeoutException

sealed class PairingResult {
  data class Accepted(val desktopId: String, val desktopName: String, val pin: String) :
    PairingResult()

  data class Rejected(val reason: String?) : PairingResult()
  object TimedOut : PairingResult()
  data class Error(val message: String) : PairingResult()
}

class PairingClient(
  private val tcpClient: TCPClient,
  private val identity: DeviceIdentity
) {
  suspend fun requestPairing(
    deviceName: String,
    onPinGenerated: (String) -> Unit,
    timeoutMs: Int = 15000
  ): PairingResult = withContext(Dispatchers.IO) {
    try {
      val keyPair = CryptoUtils.generateKeyPair()
        ?: return@withContext PairingResult.Error("Failed to generate key pair")

      val publicKeyString = CryptoUtils.encodePublicKey(keyPair.public)

      val payload = Protocol.encode(
        "PAIR_REQUEST",
        mapOf(
          "deviceId" to identity.getId(),
          "deviceName" to deviceName,
          "publicKey" to publicKeyString
        )
      )

      tcpClient.send(payload)

      val ackLine = tcpClient.receiveLine(timeoutMs)
        ?: return@withContext PairingResult.TimedOut

      val (ackType, ackFields) = Protocol.decode(ackLine)

      if (ackType != "PAIR_ACK") {
        return@withContext PairingResult.Error("Unexpected response: $ackType")
      }

      val remotePublicKeyStr = ackFields["publicKey"].orEmpty()
      val remotePublicKey = CryptoUtils.decodePublicKey(remotePublicKeyStr)
        ?: return@withContext PairingResult.Error("Invalid public key from desktop")

      val pin = CryptoUtils.calculatePin(keyPair.private, remotePublicKey)

      withContext(Dispatchers.Main) {
        onPinGenerated(pin)
      }

      val decisionLine = tcpClient.receiveLine(timeoutMs)
        ?: return@withContext PairingResult.TimedOut

      val (decisionType, decisionFields) = Protocol.decode(decisionLine)

      if (decisionType == "PAIR_DECISION" && decisionFields["accepted"] == "true") {
        PairingResult.Accepted(
          ackFields["deviceId"].orEmpty(),
          ackFields["deviceName"].orEmpty(),
          pin
        )
      } else if (decisionType == "PAIR_DECISION") {
        PairingResult.Rejected(decisionFields["reason"])
      } else {
        PairingResult.Error("Unexpected decision: $decisionType")
      }
    } catch (e: SocketTimeoutException) {
      PairingResult.TimedOut
    } catch (e: IOException) {
      PairingResult.Error(e.message ?: "IO error")
    }
  }
}