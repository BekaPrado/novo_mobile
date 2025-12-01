package com.example.app_journey.screens

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.socket.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.compose.foundation.background

@Composable
fun ChatGrupo(
    navController: NavHostController,
    grupoId: Int,
    idUsuarioAtual: Int
) {
    val socket = remember {
        SocketHandler.setSocket()
        SocketHandler.getSocket()
    }

    var mensagens by remember { mutableStateOf(listOf<Mensagem>()) }
    var texto by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 1) Entrar na sala + carregar histórico
    LaunchedEffect(Unit) {

        SocketHandler.establishConnection()
        socket.emit("join_room", grupoId)

        // BUSCAR HISTÓRICO
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.mensagensService.getMensagensPorSala(grupoId)
            }

            if (response.isSuccessful) {
                mensagens = response.body()?.mensagens ?: emptyList()
                Log.d("ChatGrupo", "Histórico carregado: ${mensagens.size} msgs")
            } else {
                Log.e("ChatGrupo", "Erro ao carregar histórico: ${response.code()}")
            }

        } catch (e: Exception) {
            Log.e("ChatGrupo", "Erro histórico: ${e.message}")
        }

        // LISTENER PARA RECEBER MENSAGENS AO VIVO
        socket.on("receive_message") { data ->
            val json = data[0] as JSONObject

            val msg = Mensagem(
                id_mensagens = 0,
                conteudo = json.getString("conteudo"),
                id_usuario = json.getInt("id_usuario"),
                id_chat_room = json.getInt("id_chat_room"),
                enviado_em = json.optString("enviado_em", ""),
                nome_completo = null,
                foto_perfil = null,
                id_chat = 0
            )

            scope.launch {
                mensagens = mensagens + msg
            }
        }
    }

    Scaffold(
        bottomBar = {
            Row(Modifier.padding(8.dp)) {

                TextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (texto.isNotBlank()) {
                            val payload = JSONObject().apply {
                                put("conteudo", texto)
                                put("id_chat_room", grupoId)
                                put("id_usuario", idUsuarioAtual)
                            }

                            socket.emit("send_message", payload)
                            texto = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Enviar")
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            items(mensagens) { msg ->

                val isMine = msg.id_usuario == idUsuarioAtual

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {

                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .background(
                                if (isMine) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                                MaterialTheme.shapes.medium
                            )
                            .padding(10.dp)
                    ) {

                        Column {
                            // Nome do remetente (se não for você)
                            if (!isMine) {
                                Text(
                                    text = "Usuário ${msg.id_usuario}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Conteúdo da bolha
                            Text(
                                text = msg.conteudo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMine) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
