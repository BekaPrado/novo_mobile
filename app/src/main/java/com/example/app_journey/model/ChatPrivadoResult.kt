package com.example.app_journey.model

data class ChatPrivadoResult(
    val status: Boolean,
    val status_code: Int,
    val conversas: List<ChatRoomPrivado>
)