package com.example.app_journey.model

data class ChatRoomResponse(
    val status: Boolean,
    val status_code: Int,
    val grupo: List<Grupo>? = null,
    val chat_room: ChatRoom? = null
)

data class ChatRoom(
    val id_chat_room: Int,
    val tipo: String,
    val id_grupo: Int? = null
)
