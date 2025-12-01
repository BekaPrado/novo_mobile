package com.example.app_journey.model

data class CalendarioResponseWrapper(
    val status: Boolean,
    val status_code: Int,
    val itens: Int?,
    val Calendario: List<CalendarioItem>?
)

data class CalendarioItem(
    val id_calendario: Int,
    val nome_evento: String,
    val data_evento: String,
    val descricao: String,
    val link: String,
    val id_grupo: Int,
    val grupo: GrupoCalendario?
)

data class GrupoCalendario(
    val id_grupo: Int,
    val nome: String,
    val limite_membros: Int,
    val descricao: String,
    val imagem: String?,
    val id_area: Int,
    val id_usuario: Int
)

data class NovoEventoRequest(
    val nome_evento: String,
    val data_evento: String,
    val descricao: String,
    val link: String,
    val id_grupo: Int,
    val id_usuario: Int
)

