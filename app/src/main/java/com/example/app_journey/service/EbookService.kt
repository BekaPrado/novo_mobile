package com.example.app_journey.service

import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.Categoria
import com.example.app_journey.model.CategoriaEbookRequest
import com.example.app_journey.model.Ebook
import com.example.app_journey.model.EbookRequest
import com.example.app_journey.model.EbookResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EbookService {
    // GET todos os eBooks
    @GET("ebook")
    suspend fun getTodosEbooks(): EbookResponseWrapper<List<Ebook>>

    // GET eBook por ID
    @GET("ebook/{id}")
    suspend fun getEbookPorId(@Path("id") id: Int): EbookResponseWrapper<Ebook>

    // POST criar novo eBook
    @POST("ebook")
    suspend fun criarEbook(@Body ebook: EbookRequest): EbookResponseWrapper<Any>

    // PUT atualizar eBook
    @PUT("ebook/{id}")
    suspend fun atualizarEbook(@Path("id") id: Int, @Body ebook: EbookRequest): EbookResponseWrapper<Any>

    // DELETE eBook
    @DELETE("ebook/{id}")
    suspend fun deleteEbook(@Path("id") id: Int): EbookResponseWrapper<Any>

    // GET todas categorias
    @GET("categoria")
    suspend fun getCategorias(): EbookResponseWrapper<List<Categoria>>

    // POST vincular categoria ao eBook
    @POST("ebook/categoria")
    suspend fun vincularCategoria(@Body categoriaEbook: CategoriaEbookRequest): EbookResponseWrapper<Any>

    // DELETE categoria do eBook
    @DELETE("ebook/categoria/{id}")
    suspend fun deleteCategoriaEbook(@Path("id") id: Int): EbookResponseWrapper<Any>
}
