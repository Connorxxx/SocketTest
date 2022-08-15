package com.connor.sockettest.server

import android.util.Log
import com.connor.sockettest.client.ClientCallback
import com.connor.sockettest.client.SocketClient
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.lang.NullPointerException
import java.net.ServerSocket
import java.net.Socket

object SocketServer {

    private const val TAG = "SocketServer"

    private val job = Job()
    private val scope = CoroutineScope(job)

    private const val SOCKET_PORT = 9527

    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null

    private lateinit var callback: ServerCallback

    private lateinit var outputStream: OutputStream

    var result = true

    fun startServer(callback: ServerCallback): Boolean {
        this.callback = callback
        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(SOCKET_PORT)
                while (result) {
                    socket = serverSocket?.accept()
                    callback.otherMsg("${socket?.inetAddress} to connected")
                    clientThread(socket!!, callback)
                }
            }  catch (e: IOException) {
                e.printStackTrace()
                result = false
            }
        }
        return result
    }

    fun stopServer() {
        socket?.apply {
            shutdownInput()
            shutdownOutput()
            close()
        }
        serverSocket?.close()
        job.cancel()
    }

    fun sendToClient(msg: String) {
        scope.launch(Dispatchers.IO) {
            if (socket != null) {
                if (socket!!.isClosed) return@launch

                outputStream = socket!!.getOutputStream()
                outputStream.apply {
                    write(msg.toByteArray())
                    flush()
                }
                callback.otherMsg("toServer: $msg")
            } else {
                Log.d(TAG, "sendToClient: null")
            }
        }
    }

    private fun clientThread(socket: Socket, callback: ServerCallback) {
        scope.launch(Dispatchers.IO) {
            val inputStream = socket.getInputStream()
            val buffer = ByteArray(1024)
            var len: Int
            var receiveStr = ""
            while (inputStream.read(buffer).also { len = it } != -1) {
                receiveStr += String(buffer, 0, len, Charsets.UTF_8)
                if (len < 1024) {
                    withContext(Dispatchers.Main) {
                        callback.receiveClientMsg(true, receiveStr)
                        receiveStr = ""
                    }

                }
            }
        }
    }
}