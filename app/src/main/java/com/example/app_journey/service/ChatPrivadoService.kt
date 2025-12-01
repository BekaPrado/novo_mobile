package com.example.app_journey.service

import com.example.app_journey.model.MensagemResponse
import com.example.app_journey.model.Usuario
import retrofit2.Response
import retrofit2.http.*

// Define o formato de resposta esperado do backend
data class ConversasPrivadasResponse(
    val status: Boolean? = null,
    val usuarios: List<Usuario>? = null,
    val mensagem: String? = null
)

// Interface Retrofit para Chat Privado
interface ChatPrivadoService {


    // Endpoint que lista todas as conversas privadas de um usuário
    @GET("usuario/{id_usuario}/conversas-privadas")
    suspend fun getConversasPrivadas(
        @Path("id_usuario") idUsuario: Int
    ): Response<ConversasPrivadasResponse>

    // Endpoint para obter ou criar uma sala de chat privada
    @POST("chat/privado")
    suspend fun obterOuCriarSalaPrivada(
        @Body body: Map<String, Int>
    ): Response<Map<String, Any>>

    // Endpoint para obter mensagens de uma sala privada
    @GET("chatroom/{id}/mensagens")
    suspend fun getMensagensPrivadas(@Path("id") idChatRoom: Int): Response<MensagemResponse>



}