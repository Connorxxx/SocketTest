package com.connor.sockettest.client

import android.util.Log
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket
import kotlin.coroutines.CoroutineContext

object SocketClient {

    private val job = Job()
    private val scope = CoroutineScope(job)

    private var socket: Socket? = null

    private var outputStream: OutputStream? = null
    private var inputStreamReader: InputStreamReader? = null

    private lateinit var callback: ClientCallback
    private const val SOCKET_PORT = 9527

    fun connectServer(ip: String, callback: ClientCallback) {
        this.callback = callback
        scope.launch(Dispatchers.IO) {
            socket = Socket(ip, SOCKET_PORT)
            clientThread(socket!!, callback)
        }
    }

    fun closeConnect() {
        inputStreamReader?.close()
        outputStream?.close()
        socket?.apply {
            shutdownInput()
            shutdownOutput()
            close()
        }
        job.cancel()
    }

    fun sendToServer(msg: String) {
        scope.launch(Dispatchers.IO) {
            if (socket!!.isClosed) return@launch
            outputStream = socket?.getOutputStream()
            outputStream?.apply {
                write(msg.toByteArray())
                flush()
            }
            callback.otherMsg("toServer: $msg")
        }
    }

    private fun clientThread(socket: Socket, callback: ClientCallback) {
        scope.launch(Dispatchers.IO) {
            val inputStream = socket.getInputStream()
            val buffer = ByteArray(1024)
            var len: Int
            var receiveStr = ""
            while (inputStream.read(buffer).also { len = it } != -1) {
                receiveStr += String(buffer, 0, len, Charsets.UTF_8)
                if (len < 1024) {
                    callback.receiveServerMsg(receiveStr)
                    receiveStr = ""
                }
            }
        }
    }
}