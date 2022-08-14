package com.connor.sockettest.model

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import com.connor.sockettest.client.SocketClient
import com.connor.sockettest.server.ServerCallback
import com.connor.sockettest.server.SocketServer

class ChatModel : BaseObservable() {

    private val TAG = "ChatModel"

    var input: String = ""
        set(value) {
            field = value
            notifyChange()
        }

    var ip: String = ""
        set(value) {
            field = value
            notifyChange()
        }

    fun isSendEnable() = input.isNotEmpty()

    fun sendToClient() {
        SocketServer.sendToClient(input)
        Log.d(TAG, "sendToClient: $input")
    }

    fun sendToServer() {
        SocketClient.sendToServer(input)
        Log.d(TAG, "sendToClient: $input")
    }
    
    fun ip() {
        Log.d(TAG, "ip: $ip")
    }

    fun getMessages(userId: Int): List<ChatMessage> {
        val messages = listOf(ChatMessage(input, userId))
        input = ""
        return messages
    }

    fun fetchHistory(): MutableList<ChatMessage> {
        return mutableListOf(
            ChatMessage("Hi I'm Server", 1),
            ChatMessage("Hi I'm Client", 2)
        )
    }
}