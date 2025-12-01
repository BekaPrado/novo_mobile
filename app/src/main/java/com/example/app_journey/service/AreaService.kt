package com.example.app_journey.service

import com.example.app_journey.model.AreaResult
import retrofit2.Call
import retrofit2.http.GET

interface AreaService {
    @GET("area")
    fun listarAreas(): Call<AreaResult>
}
