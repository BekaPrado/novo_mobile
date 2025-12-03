@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.app_journey.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.socket.SocketHandler
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.random.Random

// =========================================================
//                    TEMA DO CHAT
// =========================================================
data class ChatThemeColors(
    val background: Color,
    val surface: Color,
    val bubbleMine: Color,
    val bubbleMineBrush: List<Color>,
    val bubbleOther: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val inputBackground: Color,
    val accent: Color,
    val accentSecondary: Color,
    val divider: Color,
    val particleColor: Color,
    val isDark: Boolean
)

val DarkChatTheme = ChatThemeColors(
    background = Color(0xFF0F0A1F),
    surface = Color(0xFF1A1425),
    bubbleMine = Color(0xFF6C5CE7),
    bubbleMineBrush = listOf(Color(0xFF6C5CE7), Color(0xFF8B7CF7)),
    bubbleOther = Color(0xFF2D2640),
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.6f),
    inputBackground = Color(0xFF2D2640),
    accent = Color(0xFF9B7DFF),
    accentSecondary = Color(0xFF6C5CE7),
    divider = Color.White.copy(alpha = 0.1f),
    particleColor = Color(0xFFB39DFF),
    isDark = true
)

val LightChatTheme = ChatThemeColors(
    background = Color(0xFFF8F9FE),
    surface = Color.White,
    bubbleMine = Color(0xFF6C5CE7),
    bubbleMineBrush = listOf(Color(0xFF6C5CE7), Color(0xFF8B7CF7)),
    bubbleOther = Color(0xFFE8E5FF),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF6B7280),
    inputBackground = Color(0xFFF0EEFF),
    accent = Color(0xFF6C5CE7),
    accentSecondary = Color(0xFF8B7CF7),
    divider = Color(0xFFE5E7EB),
    particleColor = Color(0xFFD4CAFE),
    isDark = false
)

// =========================================================
//              FUNDO ANIMADO (CLARO/ESCURO)
// =========================================================
@Composable
fun AnimatedChatBackground(theme: ChatThemeColors) {
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat() * 1080f,
                y = Random.nextFloat() * 2400f,
                size = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                alpha = Random.nextFloat() * 0.4f + 0.1f
            )
        }
    }

    val infinite = rememberInfiniteTransition(label = "bg")
    val anim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 2400f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Gradiente de fundo
        val gradientColors = if (theme.isDark) {
            listOf(
                Color(0xFF0F0A1F),
                Color(0xFF1A1035),
                Color(0xFF15102A)
            )
        } else {
            listOf(
                Color(0xFFF8F9FE),
                Color(0xFFEDE9FF),
                Color(0xFFF5F3FF)
            )
        }

        drawRect(brush = Brush.verticalGradient(gradientColors))

        // Partículas
        particles.forEach { p ->
            drawCircle(
                color = theme.particleColor.copy(alpha = p.alpha),
                radius = p.size,
                center = Offset(p.x, (p.y + anim * p.speed) % size.height)
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

// =========================================================
//                  TOGGLE DE TEMA
// =========================================================
@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(56.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (isDarkTheme) Color(0xFF2D2640)
                else Color(0xFFE8E5FF)
            )
            .clickable(onClick = onToggle)
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Animação do toggle
        val offsetX by animateDpAsState(
            targetValue = if (isDarkTheme) 26.dp else 0.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "toggle"
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        if (isDarkTheme)
                            listOf(Color(0xFF6C5CE7), Color(0xFF8B7CF7))
                        else
                            listOf(Color(0xFFFFB347), Color(0xFFFFCC33))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.Star else Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// =========================================================
//                      CHAT GRUPO
// =========================================================
@Composable
fun ChatGrupo(
    navController: NavHostController,
    grupoId: Int,
    idUsuarioAtual: Int
) {
    // Estado do tema - pode ser salvo em SharedPreferences
    var isDarkTheme by remember { mutableStateOf(true) }
    val theme = if (isDarkTheme) DarkChatTheme else LightChatTheme

    // Animação de transição de cores
    val animatedBackground by animateColorAsState(
        targetValue = theme.background,
        animationSpec = tween(300),
        label = "bg"
    )
    val animatedSurface by animateColorAsState(
        targetValue = theme.surface,
        animationSpec = tween(300),
        label = "surface"
    )

    val socket = remember {
        SocketHandler.setSocket()
        SocketHandler.getSocket()
    }

    var mensagens by remember { mutableStateOf(listOf<Mensagem>()) }
    var texto by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Menu expandido
    var showMenu by remember { mutableStateOf(false) }

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

        socket.on("receive_message") { data ->
            val json = data[0] as JSONObject
            val msg = Mensagem(
                id_mensagens = 0,
                conteudo = json.getString("conteudo"),
                id_usuario = json.getInt("id_usuario"),
                id_chat_room = grupoId,
                enviado_em = "",
                nome_completo = null,
                foto_perfil = null,
                id_chat = 0
            )
            scope.launch {
                mensagens = mensagens + msg
                listState.animateScrollToItem(mensagens.size - 1)
            }
        }
    }

    LaunchedEffect(mensagens.size) {
        if (mensagens.isNotEmpty()) {
            listState.animateScrollToItem(mensagens.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background animado
        AnimatedChatBackground(theme = theme)

        Column(modifier = Modifier.fillMaxSize()) {
            // =============== TOP BAR ===============
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = animatedSurface.copy(alpha = 0.95f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão voltar
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isDarkTheme) Color.White.copy(alpha = 0.1f)
                                else Color(0xFF6C5CE7).copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = theme.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(theme.accent, theme.accentSecondary)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Info do grupo
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Chat do Grupo",
                            color = theme.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Online",
                                color = Color(0xFF10B981),
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Toggle de tema
                    ThemeToggle(
                        isDarkTheme = isDarkTheme,
                        onToggle = { isDarkTheme = !isDarkTheme }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Opções",
                                tint = theme.textPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = theme.surface
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (isDarkTheme) Icons.Default.Star else Icons.Default.Star,
                                            contentDescription = null,
                                            tint = theme.textPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            if (isDarkTheme) "Modo Claro" else "Modo Escuro",
                                            color = theme.textPrimary
                                        )
                                    }
                                },
                                onClick = {
                                    isDarkTheme = !isDarkTheme
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null,
                                            tint = theme.textPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Buscar", color = theme.textPrimary)
                                    }
                                },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = theme.textPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Info do Grupo", color = theme.textPrimary)
                                    }
                                },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                }
            }

            // =============== LISTA DE MENSAGENS ===============
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(mensagens) { msg ->
                    val isMine = msg.id_usuario == idUsuarioAtual
                    ChatBubble(
                        message = msg.conteudo,
                        isMine = isMine,
                        senderName = if (!isMine) msg.nome_completo else null,
                        theme = theme
                    )
                }

                // Mensagem vazia
                if (mensagens.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💬", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Nenhuma mensagem ainda",
                                    color = theme.textSecondary,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Seja o primeiro a enviar!",
                                    color = theme.textSecondary.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // =============== INPUT ===============
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = animatedSurface.copy(alpha = 0.98f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Botão de anexo
                    IconButton(
                        onClick = { /* anexar */ },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                theme.inputBackground,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Anexar",
                            tint = theme.accent
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Campo de texto
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(theme.inputBackground)
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        BasicTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            textStyle = TextStyle(
                                color = theme.textPrimary,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                Box {
                                    if (texto.isEmpty()) {
                                        Text(
                                            "Digite sua mensagem...",
                                            color = theme.textSecondary,
                                            fontSize = 16.sp
                                        )
                                    }
                                    inner()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botão enviar
                    val canSend = texto.isNotBlank()

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (canSend)
                                    Brush.linearGradient(theme.bubbleMineBrush)
                                else
                                    Brush.linearGradient(
                                        listOf(
                                            theme.textSecondary.copy(alpha = 0.3f),
                                            theme.textSecondary.copy(alpha = 0.3f)
                                        )
                                    )
                            )
                            .clickable(enabled = canSend) {
                                if (canSend) {
                                    val payload = JSONObject().apply {
                                        put("conteudo", texto)
                                        put("id_chat_room", grupoId)
                                        put("id_usuario", idUsuarioAtual)
                                    }
                                    socket.emit("send_message", payload)
                                    texto = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================
//                    CHAT BUBBLE
// =========================================================
@Composable
private fun ChatBubble(
    message: String,
    isMine: Boolean,
    senderName: String? = null,
    theme: ChatThemeColors
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        // Nome do remetente
        if (!isMine && senderName != null) {
            Text(
                text = senderName,
                fontSize = 12.sp,
                color = theme.accent,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isMine) 20.dp else 6.dp,
                        bottomEnd = if (isMine) 6.dp else 20.dp
                    )
                )
                .background(
                    if (isMine)
                        Brush.linearGradient(theme.bubbleMineBrush)
                    else
                        Brush.linearGradient(
                            listOf(theme.bubbleOther, theme.bubbleOther)
                        )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message,
                color = if (isMine) Color.White else theme.textPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}