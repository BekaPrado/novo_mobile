package com.example.app_journey.service

import com.example.app_journey.model.Mensagem
import retrofit2.Response
import retrofit2.http.*

data class MensagemResponse(
    val status: Boolean? = null,
    val status_code: Int? = null,
    val mensagens: List<Mensagem>? = null
)

interface MensagensService {

    // 🔥 BUSCAR HISTÓRICO DO GRUPO
    @GET("v1/journey/chatroom/{id}/mensagens")
    suspend fun getMensagensPorSala(
        @Path("id") idChatRoom: Int
    ): Response<MensagemResponse>

    // 🔥 ENVIAR PELO REST (opcional, caso precise)
    @POST("v1/journey/mensagem")
    suspend fun enviarMensagem(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<MensagemResponse>
}
