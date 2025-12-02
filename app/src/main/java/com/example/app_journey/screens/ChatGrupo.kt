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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

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

    // ENTRAR NA SALA + HISTÓRICO
    LaunchedEffect(Unit) {

        SocketHandler.establishConnection()
        socket.emit("join_room", grupoId)

        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.mensagensService.getMensagensPorSala(grupoId)
            }

            if (response.isSuccessful) {
                mensagens = response.body()?.mensagens ?: emptyList()
            }

        } catch (_: Exception) {}

        // RECEBIMENTO EM TEMPO REAL
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

            scope.launch { mensagens = mensagens + msg }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat do Grupo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home_grupo/$grupoId/$idUsuarioAtual")
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                BasicTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    decorationBox = { innerTextField ->
                        if (texto.isEmpty()) {
                            Text(
                                "Digite uma mensagem...",
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
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
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            reverseLayout = false
        ) {

            items(mensagens) { msg ->

                val isMine = msg.id_usuario == idUsuarioAtual

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMine) 16.dp else 0.dp,
                                    bottomEnd = if (isMine) 0.dp else 16.dp
                                )
                            )
                            .background(
                                if (isMine) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                            .padding(12.dp)
                    ) {

                        Column {

                            // Nome do remetente
                            if (!isMine) {
                                Text(
                                    text = "Usuário ${msg.id_usuario}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Text(
                                msg.conteudo,
                                color = if (isMine)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
