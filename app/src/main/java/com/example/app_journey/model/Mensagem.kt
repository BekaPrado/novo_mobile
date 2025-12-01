package com.example.app_journey.model

data class MensagemResponse(
    val status: Boolean,
    val status_code: Int,
    val itens: Int?,
    val mensagens: List<Mensagem>?
)

data class Mensagem(
    val id_mensagens: Int? = null,
    val conteudo: String,
    val id_chat_room: Int,
    val id_usuario: Int,
    val enviado_em: String? = null,
    var nome_completo: String? = null,
    var foto_perfil: String? = null,
    val id_chat: Int
)