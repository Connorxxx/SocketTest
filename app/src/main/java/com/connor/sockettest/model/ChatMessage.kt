package com.connor.sockettest.model

private const val currentUserId = 1

data class ChatMessage(
    val content: CharSequence,
    val userId: Int,
) {

    fun isMine(): Boolean {
        return currentUserId == userId
    }
}
