package com.connor.sockettest.client

interface ClientCallback {

    fun receiveServerMsg(msg: String)

    fun otherMsg(msg: String)

}