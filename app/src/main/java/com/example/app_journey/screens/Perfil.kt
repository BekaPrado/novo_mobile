package com.example.app_journey.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
private val AccentRed = Color(0xFFEF4444)

// =============== TELA DE PERFIL ===============
@Composable
fun Perfil(navController: NavHostController) {

    val usuarioLogado = remember { mutableStateOf<Usuario?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "perfil")
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

    // Buscar usuário
    LaunchedEffect(Unit) {
        if (idUsuario != -1) {
            RetrofitFactory()
                .getUsuarioService()
                .getUsuarioPorId(idUsuario)
                .enqueue(object : Callback<UsuarioResult> {
                    override fun onResponse(
                        call: Call<UsuarioResult>,
                        response: Response<UsuarioResult>
                    ) {
                        if (response.isSuccessful) {
                            usuarioLogado.value = response.body()?.usuario?.firstOrNull()
                            loading.value = false
                        } else {
                            errorMessage.value = "Erro ao carregar usuário"
                            loading.value = false
                        }
                    }

                    override fun onFailure(call: Call<UsuarioResult>, t: Throwable) {
                        errorMessage.value = "Erro: ${t.message}"
                        loading.value = false
                    }
                })
        } else {
            errorMessage.value = "Usuário inválido"
            loading.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // =============== HEADER COM GRADIENTE ===============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryPurple,
                                AccentPurple,
                                PrimaryPurpleDark
                            )
                        )
                    )
            )

            ProfileFloatingElements(bounce, glowAnim)

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

            // Botão configurações
            IconButton(
                onClick = { /* configurações */ },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Configurações",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // =============== CONTEÚDO PRINCIPAL ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(70.dp))

                    when {
                        loading.value -> {
                            Spacer(modifier = Modifier.height(40.dp))
                            CircularProgressIndicator(color = PrimaryPurple)
                        }

                        errorMessage.value != null -> {
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(errorMessage.value!!, color = AccentRed, fontSize = 14.sp)
                        }

                        usuarioLogado.value != null -> {
                            val usuario = usuarioLogado.value!!

                            // Nome e email
                            Text(
                                text = usuario.nome_completo ?: "Usuário",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = usuario.email ?: "",
                                fontSize = 14.sp,
                                color = TextGray
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Botão editar perfil
                            Button(
                                onClick = { navController.navigate("editar_info/$idUsuario") },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar Perfil", fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // =============== ESTATÍSTICAS (APENAS GRUPOS) ===============
                            StatsSection()

                            Spacer(modifier = Modifier.height(28.dp))

                            // =============== SOBRE MIM ===============
                            AboutSection(
                                descricao = usuario.descricao ?: "",
                                onEditClick = { navController.navigate("editar_info/$idUsuario") }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // =============== MENU DE OPÇÕES ===============
                            MenuSection(
                                onItemClick = { route ->
                                    when (route) {
                                        "meus_grupos" -> { /* navegar */ }
                                        "configuracoes" -> { /* navegar */ }
                                        "ajuda" -> { /* navegar */ }
                                        "sair" -> {
                                            context.getSharedPreferences("app_journey_prefs", Context.MODE_PRIVATE)
                                                .edit()
                                                .clear()
                                                .apply()

                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }

        // =============== FOTO DO PERFIL (SOBREPOSTA) ===============
        if (!loading.value && usuarioLogado.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 95.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .blur(30.dp)
                        .background(PrimaryPurple.copy(alpha = glowAnim * 0.4f), CircleShape)
                )

                // Container da foto
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(16.dp, CircleShape)
                        .background(CardWhite, CircleShape)
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(listOf(Color.White, SoftPurple)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (usuarioLogado.value?.foto_perfil != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(usuarioLogado.value?.foto_perfil)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryPurple, AccentPurple)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = usuarioLogado.value?.nome_completo?.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Badge de verificado
                Box(
                    modifier = Modifier
                        .offset(x = 40.dp, y = 40.dp)
                        .size(32.dp)
                        .shadow(4.dp, CircleShape)
                        .background(AccentGreen, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun ProfileFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (50 + bounce * 0.5f).dp)
            .size(80.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-30).dp, y = (120 + bounce).dp)
            .size(60.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 320.dp, y = (180 + bounce * 0.7f).dp)
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
    )

    listOf(
        Triple(50.dp, 60.dp, 6.dp),
        Triple(280.dp, 100.dp, 5.dp),
        Triple(100.dp, 200.dp, 4.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.6f), CircleShape)
        )
    }
}

// =============== SEÇÃO DE ESTATÍSTICAS (APENAS GRUPOS) ===============
@Composable
private fun StatsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = "12", label = "Grupos", color = PrimaryPurple)

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(SoftPurple)
            )

            StatItem(value = "5", label = "Criados", color = AccentGreen)

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(SoftPurple)
            )

            StatItem(value = "7", label = "Participando", color = AccentBlue)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 13.sp, color = TextGray)
    }
}

// =============== SEÇÃO SOBRE MIM ===============
@Composable
private fun AboutSection(descricao: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SoftPurple, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sobre mim",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = descricao.ifBlank { "Adicione uma descrição sobre você para que outros usuários possam te conhecer melhor! 😊" },
                fontSize = 14.sp,
                color = if (descricao.isBlank()) TextMuted else TextGray,
                lineHeight = 22.sp
            )
        }
    }
}

// =============== SEÇÃO DE MENU (SEM FAVORITOS) ===============
@Composable
private fun MenuSection(onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            MenuOptionItem(
                icon = Icons.Default.List,
                title = "Meus Grupos",
                subtitle = "Gerencie seus grupos",
                iconColor = PrimaryPurple,
                onClick = { onItemClick("meus_grupos") }
            )

            MenuDivider()

            MenuOptionItem(
                icon = Icons.Default.Settings,
                title = "Configurações",
                subtitle = "Preferências do app",
                iconColor = AccentBlue,
                onClick = { onItemClick("configuracoes") }
            )

            MenuDivider()

            MenuOptionItem(
                icon = Icons.Default.Info,
                title = "Ajuda",
                subtitle = "Dúvidas e suporte",
                iconColor = AccentGreen,
                onClick = { onItemClick("ajuda") }
            )

            MenuDivider()

            MenuOptionItem(
                icon = Icons.Default.ExitToApp,
                title = "Sair",
                subtitle = "Encerrar sessão",
                iconColor = AccentRed,
                onClick = { onItemClick("sair") }
            )
        }
    }
}

@Composable
private fun MenuOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }

        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(1.dp)
            .background(SoftPurple.copy(alpha = 0.5f))
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPerfil() {
    Perfil(navController = rememberNavController())
}