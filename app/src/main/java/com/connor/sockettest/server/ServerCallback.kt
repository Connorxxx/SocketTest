package com.connor.sockettest.server

interface ServerCallback {

    fun receiveClientMsg(success: Boolean, msg: String)
    fun otherMsg(msg: String)

}