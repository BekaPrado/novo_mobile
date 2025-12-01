package com.example.app_journey.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResponse
import com.google.gson.Gson

object SharedPrefHelper {
    private const val PREF_NAME = "journey_prefs"
    private const val KEY_EMAIL = "email_usuario"
    private const val KEY_USER_ID = "id_usuario"
    private const val KEY_USUARIO_CACHE = "usuario_cache"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ---------------- Email ----------------
    fun salvarEmail(context: Context, email: String) {
        getPreferences(context).edit().putString(KEY_EMAIL, email).apply()
    }

    fun recuperarEmail(context: Context): String? {
        return getPreferences(context).getString(KEY_EMAIL, null)
    }

    // ---------------- Usuário ----------------
    fun salvarUsuario(context: Context, usuario: UsuarioResponse) {
        val usuarioJson = Gson().toJson(usuario)
        getPreferences(context).edit().putString(KEY_USUARIO_CACHE, usuarioJson).apply()
    }

    fun recuperarUsuario(context: Context): Usuario? {
        val usuarioJson = getPreferences(context).getString(KEY_USUARIO_CACHE, null)
        return usuarioJson?.let { Gson().fromJson(it, Usuario::class.java) }
    }

    // ---------------- ID ----------------
    fun salvarIdUsuario(context: Context, id: Int) {
        getPreferences(context).edit().putInt(KEY_USER_ID, id).apply()
    }

    fun recuperarIdUsuario(context: Context): Int? {
        val id = getPreferences(context).getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    // ---------------- Logout ----------------
    fun deslogar(context: Context) {
        getPreferences(context).edit()
            .remove(KEY_EMAIL)
            .remove(KEY_USER_ID)
            .remove(KEY_USUARIO_CACHE)
            .apply()
    }
}
