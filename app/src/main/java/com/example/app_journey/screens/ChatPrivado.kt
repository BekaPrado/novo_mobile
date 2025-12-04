package com.example.app_journey.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// =============== PALETA DE CORES ===============
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF5849C2)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val BackgroundWhite = Color(0xFFFAFAFF)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF6B7280)
private val TextMuted = Color(0xFF9CA3AF)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)

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
    var isLoading by remember { mutableStateOf(true) }
    var isTyping by remember { mutableStateOf(false) }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "chat")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Animação de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    // Carrega histórico
    LaunchedEffect(idChatRoom) {
        try {
            val response = RetrofitInstance.mensagensService.getMensagensPorSala(idChatRoom)
            if (response.isSuccessful) {
                mensagens = response.body()?.mensagens ?: emptyList()
                if (mensagens.isNotEmpty()) {
                    listState.animateScrollToItem(mensagens.size - 1)
                }
            } else {
                Log.e("ChatPrivado", "Erro ao buscar histórico: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ChatPrivado", "Erro: ${e.localizedMessage}")
        } finally {
            isLoading = false
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
        ) {
            // =============== HEADER CUSTOMIZADO ===============
            ChatHeader(
                nomeUsuario = nomeOutroUsuario,
                isOnline = true,
                glowAnim = glowAnim,
                onBackClick = {
                    SocketHandler.leaveRoom(idChatRoom)
                    navController.popBackStack()
                },
                onInfoClick = { /* Info do chat */ }
            )

            // =============== ÁREA DE MENSAGENS ===============
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryPurple)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Carregando mensagens...",
                                    color = TextGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    mensagens.isEmpty() -> {
                        // Empty state
                        EmptyChatState(nomeOutroUsuario)
                    }

                    else -> {
                        // Lista de mensagens
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            state = listState,
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(mensagens) { index, msg ->
                                val isMinhaMsg = msg.id_usuario == idUsuario
                                val showAvatar = !isMinhaMsg && (index == 0 || mensagens[index - 1].id_usuario == idUsuario)

                                MessageBubble(
                                    mensagem = msg,
                                    isMinhaMsg = isMinhaMsg,
                                    showAvatar = showAvatar,
                                    index = index
                                )
                            }

                            // Indicador de digitando
                            if (isTyping) {
                                item {
                                    TypingIndicator()
                                }
                            }
                        }
                    }
                }
            }

            // =============== INPUT DE MENSAGEM ===============
            MessageInput(
                value = novaMensagem,
                onValueChange = { novaMensagem = it },
                onSendClick = {
                    if (novaMensagem.isNotBlank()) {
                        val json = JSONObject().apply {
                            put("conteudo", novaMensagem)
                            put("id_chat_room", idChatRoom)
                            put("id_usuario", idUsuario)
                        }
                        SocketHandler.sendMessage(json)
                        novaMensagem = ""
                    }
                }
            )
        }
    }
}

// =============== HEADER DO CHAT ===============
@Composable
private fun ChatHeader(
    nomeUsuario: String,
    isOnline: Boolean,
    glowAnim: Float,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(PrimaryPurple, AccentPurple)
                )
            )
            .statusBarsPadding()
    ) {
        // Elementos decorativos
        Box(
            modifier = Modifier
                .offset(x = 300.dp, y = 10.dp)
                .size(60.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-20).dp, y = 30.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão voltar
            IconButton(onClick = onBackClick) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Avatar do usuário
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentBlue, AccentPurple)
                            ),
                            CircleShape
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nomeUsuario.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Indicador online
                if (isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .background(AccentGreen, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nome e status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nomeUsuario,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isOnline) AccentGreen else TextMuted,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "Online" else "Offline",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Botões de ação
            IconButton(onClick = { /* Chamada de voz */ }) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Ligar",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Mais",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// =============== ESTADO VAZIO ===============
@Composable
private fun EmptyChatState(nomeUsuario: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Ícone animado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(SoftPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💬", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Iniciar conversa",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Envie uma mensagem para $nomeUsuario\ne comece uma nova conversa!",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// =============== BOLHA DE MENSAGEM ===============
@Composable
private fun MessageBubble(
    mensagem: Mensagem,
    isMinhaMsg: Boolean,
    showAvatar: Boolean,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300, delayMillis = minOf(index * 50, 500))) +
                slideInHorizontally(
                    tween(300, delayMillis = minOf(index * 50, 500)),
                    initialOffsetX = { if (isMinhaMsg) 100 else -100 }
                )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isMinhaMsg) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            // Avatar do outro usuário
            if (!isMinhaMsg) {
                if (showAvatar) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.linearGradient(listOf(AccentBlue, AccentPurple)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "U",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            // Bolha da mensagem
            Column(
                horizontalAlignment = if (isMinhaMsg) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (isMinhaMsg) 20.dp else 4.dp,
                                bottomEnd = if (isMinhaMsg) 4.dp else 20.dp
                            )
                        )
                        .background(
                            if (isMinhaMsg) {
                                Brush.horizontalGradient(
                                    colors = listOf(PrimaryPurple, AccentPurple)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(CardWhite, CardWhite)
                                )
                            },
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (isMinhaMsg) 20.dp else 4.dp,
                                bottomEnd = if (isMinhaMsg) 4.dp else 20.dp
                            )
                        )
                        .widthIn(max = 280.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = mensagem.conteudo,
                        fontSize = 15.sp,
                        color = if (isMinhaMsg) Color.White else TextDark,
                        lineHeight = 22.sp
                    )
                }

                // Horário
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatarHora(mensagem.enviado_em),
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Espaço para alinhar mensagens próprias
            if (isMinhaMsg) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

// =============== INDICADOR DE DIGITANDO ===============
@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = Modifier.padding(start = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(CardWhite, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { index ->
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(index * 200)
                        ),
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer { this.alpha = alpha }
                            .background(PrimaryPurple, CircleShape)
                    )
                }
            }
        }
    }
}

// =============== INPUT DE MENSAGEM ===============
@Composable
private fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val hasText = value.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Botão de anexo
            IconButton(
                onClick = { /* Anexar arquivo */ },
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SoftPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Anexar",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Campo de texto
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Digite sua mensagem...",
                        color = TextMuted,
                        fontSize = 15.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = SoftPurple,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite,
                    cursorColor = PrimaryPurple
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botão de enviar
            AnimatedContent(
                targetState = hasText,
                transitionSpec = {
                    scaleIn(tween(200)) togetherWith scaleOut(tween(200))
                },
                label = "sendButton"
            ) { showSend ->
                if (showSend) {
                    IconButton(
                        onClick = onSendClick,
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(PrimaryPurple, AccentPurple)
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { /* Gravar áudio */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(SoftPurple, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Gravar",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============== FUNÇÃO AUXILIAR ===============
private fun formatarHora(dataString: String?): String {
    if (dataString.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        ""
    }
}