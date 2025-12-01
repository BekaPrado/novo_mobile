package com.example.app_journey.model

import com.google.gson.annotations.SerializedName

data class Area(
    val id_area: Int,
    val area: String
)

data class AreaResult(
    val status: Boolean,
    val status_code: Int,
    val itens: Int,

    @SerializedName("Area")
    val areas: List<Area>
)
