package com.example.app_journey.model

data class GruposResult(
    val status: Boolean,
    val status_code: Int,
    val itens: Int,
    val grupos: List<Grupo>
)

data class GrupoWrapper(
    val status: Boolean,
    val status_code: Int,
    val grupo: Grupo?     // 👈 AGORA É UM OBJETO, NÃO UMA LISTA
)

data class Grupo(
    val id_grupo: Int? = null,
    val nome: String,
    val limite_membros: Int,
    val descricao: String,
    val imagem: String?,
    val id_area: Int,
    val id_usuario: Int
)
