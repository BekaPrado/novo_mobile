package com.example.app_journey.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =============== PALETA DE CORES ===============
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF5849C2)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val LightPurple = Color(0xFFF3F1FF)
private val BackgroundWhite = Color(0xFFFAFAFF)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF6B7280)
private val TextMuted = Color(0xFF9CA3AF)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentRed = Color(0xFFEF4444)
private val AccentPink = Color(0xFFEC4899)

@Composable
fun ConversasPrivadasScreen(
    navController: NavHostController,
    idUsuario: Int
) {
    var conversas by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "conversas")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Animação de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "alpha"
    )

    // Carregar conversas
    LaunchedEffect(idUsuario) {
        scope.launch {
            try {
                Log.d("ConversasPrivadas", "Buscando conversas para o usuário: $idUsuario")

                val response = try {
                    withContext(Dispatchers.IO) {
                        RetrofitInstance.usuarioService.listarUsuarios().execute()
                    }
                } catch (e: Exception) {
                    Log.e("ConversasPrivadas", "Erro ao buscar usuários: ${e.message}")
                    null
                }

                if (response?.isSuccessful == true) {
                    response.body()?.let { result ->
                        if (result.status && result.usuario.isNotEmpty()) {
                            val listaUsuarios = result.usuario.filter { it.id_usuario != idUsuario }
                            Log.d("ConversasPrivadas", "${listaUsuarios.size} usuários carregados")
                            conversas = listaUsuarios
                        } else {
                            erro = "Nenhum usuário encontrado"
                        }
                    } ?: run {
                        erro = "Resposta inválida do servidor"
                    }
                } else {
                    erro = "Erro ao carregar conversas"
                }
            } catch (e: Exception) {
                erro = "Erro: ${e.message}"
            } finally {
                carregando = false
            }
        }
    }

    // Filtro de busca
    val conversasFiltradas = conversas.filter {
        it.nome_completo.contains(searchText, ignoreCase = true) ||
                (it.email?.contains(searchText, ignoreCase = true) == true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // =============== HEADER ===============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PrimaryPurple, AccentPurple, PrimaryPurpleDark)
                        )
                    )
            )

            // Elementos decorativos
            ConversasFloatingElements(bounce, glowAnim)

            // Botão voltar
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Título
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Conversas",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${conversas.size} contatos disponíveis",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // =============== CONTEÚDO PRINCIPAL ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Spacer(modifier = Modifier.height(160.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    // =============== SEARCH BAR ===============
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Buscar conversa...", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpar", tint = TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            cursorColor = PrimaryPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== CONTEÚDO ===============
                    when {
                        carregando -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PrimaryPurple)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Carregando conversas...",
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        erro != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "😕", fontSize = 60.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = erro ?: "Erro desconhecido",
                                        color = AccentRed,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        conversasFiltradas.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "💬", fontSize = 60.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Nenhuma conversa encontrada",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Tente buscar por outro nome",
                                        fontSize = 14.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(conversasFiltradas) { index, usuario ->
                                    ModernConversaItem(
                                        usuario = usuario,
                                        index = index,
                                        onClick = {
                                            navController.navigate("chatPrivado/${usuario.id_usuario}/${usuario.nome_completo}/${idUsuario}")
                                        }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun ConversasFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (30 + bounce * 0.5f).dp)
            .size(70.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-20).dp, y = (80 + bounce).dp)
            .size(50.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 330.dp, y = (120 + bounce * 0.7f).dp)
            .size(35.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
    )

    listOf(
        Triple(60.dp, 40.dp, 5.dp),
        Triple(270.dp, 70.dp, 4.dp),
        Triple(150.dp, 100.dp, 3.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.6f), CircleShape)
        )
    }
}

// =============== ITEM DE CONVERSA MODERNO ===============
@Composable
private fun ModernConversaItem(
    usuario: Usuario,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val colors = listOf(AccentPurple, AccentBlue, AccentPink, AccentGreen, PrimaryPurple)
    val accentColor = colors[index % colors.size]

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300, delayMillis = index * 50)) +
                slideInHorizontally(tween(300, delayMillis = index * 50)) { it / 4 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box {
                    if (usuario.foto_perfil != null) {
                        AsyncImage(
                            model = usuario.foto_perfil,
                            contentDescription = "Foto de ${usuario.nome_completo}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(accentColor, accentColor.copy(alpha = 0.7f))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = usuario.nome_completo.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Indicador online
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .background(AccentGreen, CircleShape)
                            .border(2.dp, CardWhite, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.nome_completo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = usuario.email ?: "Sem email",
                        fontSize = 13.sp,
                        color = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Seta e indicador de mensagem
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Badge de mensagens não lidas (exemplo)
                    if (index % 3 == 0) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(AccentPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${(index % 5) + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewConversasPrivadas() {
    ConversasPrivadasScreen(
        navController = rememberNavController(),
        idUsuario = 1
    )
}