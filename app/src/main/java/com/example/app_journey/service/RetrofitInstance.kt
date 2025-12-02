package com.example.app_journey.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:3030/v1/journey/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val usuarioService: UsuarioService = retrofit.create(UsuarioService::class.java)
    val grupoService: GrupoService = retrofit.create(GrupoService::class.java)
    val calendarioService: CalendarioService = retrofit.create(CalendarioService::class.java)

    // 🔥 ESTE É O SEU SERVICE LEGÍTIMO
    val mensagensService: MensagensService = retrofit.create(MensagensService::class.java)

    val chatPrivadoService: ChatPrivadoService = retrofit.create(ChatPrivadoService::class.java)
    val ebookService: EbookService = retrofit.create(EbookService::class.java)
    val chatRoomService: ChatRoomService = retrofit.create(ChatRoomService::class.java)

    val areaService: AreaService by lazy {
        retrofit.create(AreaService::class.java)
    }

}
