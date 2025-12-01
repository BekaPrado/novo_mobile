package com.example.app_journey.model

data class ChatRoomPrivado(
    val id_chat_room: Int,
    val id_usuario_1: Int,
    val id_usuario_2: Int,
    val nomeOutroUsuario: String
)