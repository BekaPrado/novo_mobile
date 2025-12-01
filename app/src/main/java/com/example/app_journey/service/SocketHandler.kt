package com.example.app_journey.socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {
    private lateinit var socket: Socket

    fun setSocket() {
        try {
            socket = IO.socket("http://192.168.15.27:3030")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    fun getSocket(): Socket {
        return socket
    }

    fun establishConnection() {
        socket.connect()
    }

    fun closeConnection() {
        socket.disconnect()
    }
}
