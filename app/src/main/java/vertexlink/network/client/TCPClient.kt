package com.vertexlink.network

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

class TCPClient(private val host: String, private val port: Int) {
  private var socket: Socket? = null
  private var writer: BufferedWriter? = null
  private var reader: BufferedReader? = null

  @Throws(IOException::class)
  fun connect(timeoutMs: Int = 5000) {
    val s = Socket()

    s.connect(InetSocketAddress(host, port), timeoutMs)

    socket = s
    writer = BufferedWriter(OutputStreamWriter(s.getOutputStream()))
    reader = BufferedReader(InputStreamReader(s.getInputStream()))
  }

  @Throws(IOException::class)
  fun send(message: String) {
    val w = writer ?: throw IOException("Not connected")

    synchronized(this) {
      w.write(message)
      w.write("\n")
      w.flush()
    }
  }

  @Throws(IOException::class)
  fun receiveLine(timeoutMs: Int? = null): String? {
    val s = socket ?: throw IOException("Not connected")

    if (timeoutMs != null) s.soTimeout = timeoutMs

    return try {
      reader?.readLine()
    } catch (e: SocketTimeoutException) {
      throw e
    }
  }

  fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false

  fun close() {
    try {
      reader?.close()
    } catch (_: Exception) {
    }
    try {
      writer?.close()
    } catch (_: Exception) {
    }
    try {
      socket?.close()
    } catch (_: Exception) {
    }
  }
}