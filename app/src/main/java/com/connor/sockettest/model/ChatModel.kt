package com.connor.sockettest.model

import android.util.Log
import androidx.databinding.BaseObservable
import com.connor.sockettest.client.SocketClient
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

    fun getMessages(userId: Int): List<ChatMessage> {
        val messages = listOf(ChatMessage(input, userId))
        input = ""
        return messages
    }

    fun getMsg(userId: Int, msg: String): List<ChatMessage> {
        return listOf(ChatMessage(msg, userId))
    }

    fun fetchHistory(): MutableList<ChatMessage> {
        return mutableListOf(
            ChatMessage("Hi I'm Server", 1),
            ChatMessage("Hi I'm Client", 2)
        )
    }
}