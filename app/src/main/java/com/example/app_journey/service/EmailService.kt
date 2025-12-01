package com.example.app_journey.service

import com.example.app_journey.model.EmailRequest
import com.example.app_journey.model.ValidacaoResponse
import retrofit2.Call
import retrofit2.http.*

interface EmailService {
    @POST("recuperacao-senha")
    fun enviarEmail(@Body request: EmailRequest): Call<Void>

    @POST("verificar-email")
    fun validarCodigo(@Body body: Map<String, String>): Call<ValidacaoResponse>
}