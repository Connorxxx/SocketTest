package com.connor.sockettest.ui

import android.annotation.SuppressLint
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.connor.sockettest.R
import com.connor.sockettest.databinding.FragmentServerBinding
import com.connor.sockettest.model.ChatMessage
import com.connor.sockettest.model.ChatModel
import com.connor.sockettest.server.ServerCallback
import com.connor.sockettest.server.SocketServer
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.base.EngineFragment

class ServerFragment : EngineFragment<FragmentServerBinding>(R.layout.fragment_server) , ServerCallback {

    companion object {
        private const val TAG = "ServerFragment"
    }

    private val model = ChatModel()

    //Socket服务是否打开
    var openSocket = false

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        binding.v = this
        binding.m = model
        binding.tvIp.text = "IP Address: ${getIp()}"
        binding.rv.setup {
            addType<ChatMessage> {
                if (isMine()) R.layout.item_msg_right else R.layout.item_msg_left
            }
        }
    }

    override fun initData() {
        binding.rv.models = model.fetchHistory()

    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnNext -> {
                findNavController().navigate(R.id.action_serverFragment_to_clientFragment)
            }
            binding.btnOpen -> {
                openSocket = if (openSocket) { SocketServer.stopServer(); false }
                else { SocketServer.startServer(this); true }
                Log.d(TAG, "onClick: $openSocket")
                showMsg(if (openSocket) "Opened Server" else "Closed Server")
                binding.btnOpen.text = if (openSocket) "Close Server" else "Open Server"
            }
            binding.btnSend -> {
                if (!openSocket) showMsg("please open server first") else {
                    model.sendToClient()
                    binding.rv.addModels(model.getMessages(1))
                    Log.d(TAG, "onClickSend: ${model.getMessages(1)}")
                    binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount - 1)
                }
            }
        }
    }

    private fun getIp() =
        intToIp((activity?.applicationContext?.getSystemService(WIFI_SERVICE) as WifiManager)
                .connectionInfo.ipAddress)

    private fun intToIp(ip: Int) =
        "${(ip and 0xFF)}.${(ip shr 8 and 0xFF)}.${(ip shr 16 and 0xFF)}.${(ip shr 24 and 0xFF)}"

    private fun showMsg(msg: String) = Toast.makeText(activity?.applicationContext, msg, Toast.LENGTH_SHORT).show()

    override fun receiveClientMsg(success: Boolean, msg: String) {
        binding.rv.addModels(model.getMessages(2))
    }

    override fun otherMsg(msg: String) {
        Log.d(TAG, msg)
    }
}