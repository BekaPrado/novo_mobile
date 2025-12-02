package com.example.app_journey.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitEmailFactory {
    fun getEmailService(): EmailService {
        return Retrofit.Builder()
            .baseUrl("http://10.107.134.16:3030/v1/journey/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmailService::class.java)
    }
}