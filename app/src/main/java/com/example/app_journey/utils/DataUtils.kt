package com.example.app_journey.utils


import java.text.SimpleDateFormat
import java.util.*
import kotlin.let
import kotlin.text.isNullOrBlank
import kotlin.text.startsWith

fun String.formatarData(): String {
    return try {
        val formatoOriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoDestino = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val data = formatoOriginal.parse(this)
        data?.let { formatoDestino.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun isImagemValida(url: String?): Boolean {
    return !url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://"))
}