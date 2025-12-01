package com.example.app_journey.service

import com.example.app_journey.model.*
import retrofit2.Call
import retrofit2.http.*

interface GrupoService {

    // Criar grupo
    @Headers("Content-Type: application/json")
    @POST("group")
    fun inserirGrupo(@Body grupo: Grupo): Call<GruposResult>

    // Listar grupos
    @GET("group")
    fun listarGrupos(): Call<GruposResult>

    // Buscar grupo por ID (única versão)
    @GET("group/{id}")
    fun getGrupoById(@Path("id") id: Int): Call<GrupoWrapper>

    // Listar grupos do usuário (participando)
    @GET("usuario/{id}/grupos-participando")
    fun listarGruposParticipando(@Path("id") idUsuario: Int): Call<GruposResult>

    // Listar grupos criados
    @GET("usuario/{id}/grupos-criados")
    fun listarGruposCriados(@Path("id") idUsuario: Int): Call<GruposResult>

    // Entrar no grupo
    @POST("group/{id}/join")
    fun participarDoGrupo(
        @Path("id") id_grupo: Int?,
        @Body body: Map<String, Int>
    ): Call<ApiResponse>
}
