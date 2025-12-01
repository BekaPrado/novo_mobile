package com.example.app_journey.service

import com.example.app_journey.model.ChatRoomResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatRoomService {
    @GET("v1/journey/group/chat-room/{id}")
    fun getChatRoomByGroup(
        @Path("id") idGrupo: Int
    ): Call<ChatRoomResponse>
}
