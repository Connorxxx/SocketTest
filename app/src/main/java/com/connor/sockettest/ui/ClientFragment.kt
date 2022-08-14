package com.connor.sockettest.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.connor.sockettest.R
import com.connor.sockettest.client.ClientCallback
import com.connor.sockettest.client.SocketClient
import com.connor.sockettest.databinding.FragmentClientBinding
import com.connor.sockettest.model.ChatMessage
import com.connor.sockettest.model.ChatModel
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineFragment

class ClientFragment : EngineFragment<FragmentClientBinding>(R.layout.fragment_client) , ClientCallback {

    private val TAG = "ClientFragment"
    private val model = ChatModel()

    //Socket服务是否连接
    private var connectSocket = false

    override fun initView() {
        binding.v = this
        binding.m = model
        binding.rv.setup {
            addType<ChatMessage> {
                if (!isMine()) R.layout.item_msg_right else R.layout.item_msg_left
            }
        }
    }

    override fun initData() {
        binding.rv.models = model.fetchHistory()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnLast -> {
                findNavController().navigate(R.id.action_clientFragment_to_serverFragment)
            }
            binding.btnConnect -> {
                // if (model.getIP().isEmpty()) showMsg("Please input IP")
                val ip = binding.etIpAddress.text.toString()
                if (ip.isEmpty()) showMsg("Please input IP") else {
                    connectSocket = if (connectSocket) {
                        SocketClient.closeConnect(); false
                    } else {
                        SocketClient.connectServer(ip, this); true
                    }
                    showMsg(if (connectSocket) "Connected" else "Closed")
                    binding.btnConnect.text = if (connectSocket) "Close Server" else "Connect Server"
                    Log.d(TAG, "onClickTrue: $connectSocket")
                }
            }
            binding.btnSend -> {
                if (!connectSocket) showMsg("please connect server first") else {
                    model.sendToServer()
                    binding.rv.addModels(model.getMessages(2))
                    Log.d(TAG, "onClickSend: ${model.getMessages(2)}")
                    binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount - 1)
                }
            }
        }
    }

    override fun receiveServerMsg(msg: String) {
        binding.rv.addModels(model.getMessages(1))
    }

    override fun otherMsg(msg: String) {
        Log.d(TAG, msg)
    }

    private fun showMsg(msg: String) = Toast.makeText(activity?.applicationContext, msg, Toast.LENGTH_SHORT).show()
}


