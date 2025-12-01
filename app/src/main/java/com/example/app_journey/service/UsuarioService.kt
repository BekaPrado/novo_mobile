package com.example.app_journey.service


import com.example.app_journey.model.LoginRequest
import com.example.app_journey.model.LoginResponse
import com.example.app_journey.model.Result
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResult
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {
    @Headers("Content-Type: application/json")
    @POST("usuario")
    fun inserirUsuario(@Body usuario: Usuario): Call<Usuario>

    @GET("usuario")
    fun listarUsuarios(): Call<Result>

    @GET("usuario/{id}")
    fun getUsuarioPorId(@Path("id") id: Int): Call<UsuarioResult>

    @GET("usuario/{id}")
    suspend fun getUsuarioPorIdSuspend(@Path("id") id: Int): UsuarioResult

    @Headers("Content-Type: application/json")
    @PUT("usuario/{id}")
    fun atualizarUsuarioPorId(
        @Path("id") id: Int,
        @Body usuarioAtualizado: Usuario?
    ): Call<Usuario>


    @POST("usuario/login")
    fun loginUsuario(@Body body: LoginRequest): Call<LoginResponse>

    @PUT("usuario/senha/{id}")
    @Headers("Content-Type: application/json")
    fun redefinirSenhaRaw(
        @Path("id") id: Int,
        @Body body: okhttp3.RequestBody
    ): Call<Void>
}