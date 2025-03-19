package com.example.chatapplication

import com.google.firebase.Timestamp

data class Message(
    val message: String = "",
    val senderId: String = "",
    val timestamp: Timestamp? = null
)
