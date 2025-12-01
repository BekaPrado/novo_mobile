package com.example.app_journey.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    private var socket: Socket? = null
    private const val BASE_URL = "http://10.0.2.2:3030" // emulador Android → back local

    /**
     * Inicializa o socket.io (só uma vez)
     */
    fun init(baseUrl: String = BASE_URL) {
        try {
            if (socket == null) {
                socket = IO.socket(baseUrl)
                Log.d("SocketHandler", "✅ Socket inicializado em $baseUrl")
            }
        } catch (e: URISyntaxException) {
            Log.e("SocketHandler", "❌ Erro URI Socket: ${e.message}")
        }
    }

    /**
     * Conecta o socket
     */
    fun connect() {
        if (socket == null) init()
        if (socket?.connected() == false) {
            socket?.connect()
            Log.d("SocketHandler", "🔌 Conectado ao servidor Socket.IO")
        }
    }

    /**
     * Retorna o socket atual
     */
    fun getSocket(): Socket? = socket

    /**
     * Entrar numa sala
     */
    fun joinRoom(roomId: Int) {
        socket?.emit("join_room", roomId)
        Log.d("SocketHandler", "🚪 Entrou na sala $roomId")
    }

    /**
     * Sair da sala
     */
    fun leaveRoom(roomId: Int) {
        socket?.emit("leave_room", roomId)
        Log.d("SocketHandler", "🚶 Saiu da sala $roomId")
    }

    /**
     * Desconectar completamente
     */
    fun disconnect() {
        socket?.disconnect()
        socket = null
        Log.d("SocketHandler", "🔴 Socket desconectado e liberado")
    }
    /**
     * Envia uma mensagem em tempo real
     */
    fun sendMessage(jsonData: org.json.JSONObject) {
        if (socket?.connected() == true) {
            socket?.emit("send_message", jsonData)
            Log.d("SocketHandler", "📨 Mensagem emitida via socket: $jsonData")
        } else {
            Log.w("SocketHandler", "⚠️ Tentou enviar mensagem mas o socket não está conectado")
        }
    }

}