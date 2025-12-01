package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPrivadoScreen(
    navController: NavHostController,
    idChatRoom: Int,
    idUsuario: Int,
    nomeOutroUsuario: String
) {
    val coroutineScope = rememberCoroutineScope()
    val socket = remember { SocketHandler.getSocket() }
    val listState = rememberLazyListState()

    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var novaMensagem by remember { mutableStateOf("") }
    var socketInitialized by remember { mutableStateOf(false) }

    // Carrega histórico
    LaunchedEffect(idChatRoom) {
        try {
            val response = RetrofitInstance.mensagensService.getMensagensPorSala(idChatRoom)

            if (response.isSuccessful) {
                mensagens = response.body()?.mensagens ?: emptyList()
            } else {
                Log.e("ChatPrivado", "Erro ao buscar histórico: ${response.code()}")
            }

        } catch (e: Exception) {
            Log.e("ChatPrivado", "Erro: ${e.localizedMessage}")
        }
    }

    // Socket.io
    LaunchedEffect(Unit) {
        if (!socketInitialized) {
            SocketHandler.init()
            SocketHandler.connect()
            SocketHandler.joinRoom(idChatRoom)
            socketInitialized = true
        }

        socket?.on("receive_message") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject

                val msg = Mensagem(
                    id_mensagens = data.optInt("id_mensagens"),
                    conteudo = data.optString("conteudo"),
                    id_chat_room = data.optInt("id_chat_room"),
                    id_usuario = data.optInt("id_usuario"),
                    enviado_em = data.optString("enviado_em"),
                    nome_completo = null,
                    foto_perfil = null,
                    id_chat = idChatRoom
                )

                coroutineScope.launch(Dispatchers.Main) {
                    mensagens = mensagens + msg
                    listState.animateScrollToItem(mensagens.size - 1)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nomeOutroUsuario, fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF341E9B)),
                navigationIcon = {
                    IconButton(onClick = {
                        SocketHandler.leaveRoom(idChatRoom)
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/2223/2223615.png"),
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFEDEEFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {

            // Lista de mensagens
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(mensagens.size) { index ->
                    val msg = mensagens[index]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.id_usuario == idUsuario)
                            Arrangement.End else Arrangement.Start
                    ) {
                        if (msg.id_usuario != idUsuario) {
                            Image(
                                painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/149/149071.png"),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Column(
                            modifier = Modifier
                                .background(
                                    if (msg.id_usuario == idUsuario) Color(0xFFB5A9FF) else Color.White,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                                .widthIn(max = 260.dp)
                        ) {
                            Text(
                                text = msg.conteudo,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Input de mensagem
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = novaMensagem,
                    onValueChange = { novaMensagem = it },
                    placeholder = { Text("Digite uma mensagem...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (novaMensagem.isNotBlank()) {
                            val json = JSONObject().apply {
                                put("conteudo", novaMensagem)
                                put("id_chat_room", idChatRoom)
                                put("id_usuario", idUsuario)
                            }

                            SocketHandler.sendMessage(json)
                            novaMensagem = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF341E9B))
                ) {
                    Text("Enviar", color = Color.White)
                }
            }
        }
    }
}
