package com.example.app_journey.service

import com.example.app_journey.model.CalendarioItem
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import retrofit2.Call
import retrofit2.http.*

interface CalendarioService {

    // GET: todos os eventos
    @GET("calendario")
    fun getTodosEventos(): Call<CalendarioResponseWrapper>

    // GET: evento específico por id
    @GET("calendario/:id")
    fun getEventoPorId(
        @Path("id") idEvento: Int
    ): Call<CalendarioResponseWrapper>

    // POST: criar novo evento
    @POST("calendario")
    fun criarEvento(
        @Body evento: NovoEventoRequest
    ): Call<CalendarioResponseWrapper>

    @DELETE("calendario/{id}")
    fun excluirEvento(@Path("id") id: Int): Call<CalendarioResponseWrapper>





}
