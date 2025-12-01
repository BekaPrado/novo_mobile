package com.example.app_journey.model
data class Usuario(
    val id_usuario: Int? = null,
    val nome_completo: String,
    val email: String,
    val data_nascimento: String?,
    val foto_perfil: String?,
    val descricao: String?,
    val senha: String,
    val tipo_usuario: String
)
data class SenhaRequest(
    val senha: String = ""
)
data class LoginResponse(
    val status: Boolean,
    val status_code: Int,
    val message: String,
    val token: String?,
    val usuario: UsuarioResponse?
)

data class UsuarioResponse(
    val id: Int,
    val nome: String,
    val email: String,
    val tipo_usuario: String,
    val linkedin_url: String?
)
data class LoginRequest(
    val email: String,
    val senha: String
)

data class UsuarioResult(
    val status: Boolean,
    val status_code: Int,
    val usuario: List<Usuario>?
)