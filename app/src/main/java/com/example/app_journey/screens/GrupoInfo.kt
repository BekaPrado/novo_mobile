package com.example.app_journey.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Cores do tema
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF4A3CB5)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val BackgroundLight = Color(0xFFF8F9FE)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF10B981)
private val CardWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrupoInfo(
    navController: NavHostController,
    grupoId: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var grupo by remember { mutableStateOf<com.example.app_journey.model.Grupo?>(null) }
    var participando by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    LaunchedEffect(grupoId) {
        if (grupoId <= 0) {
            carregandoDados = false
            erroMsg = "Grupo inválido (ID $grupoId)"
            return@LaunchedEffect
        }

        try {
            withContext(Dispatchers.IO) {
                val response = RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
                if (response.isSuccessful) {
                    grupo = response.body()?.grupos?.firstOrNull()
                } else {
                    erroMsg = "Erro ao carregar grupo: ${response.code()}"
                }
            }
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }

    val nome = grupo?.nome ?: "Grupo sem nome"
    val descricao = grupo?.descricao ?: "Sem descrição"
    val imagem = grupo?.imagem ?: ""
    val membros = grupo?.limite_membros ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Background com gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PrimaryPurple,
                            PrimaryPurpleDark
                        )
                    )
                )
        )

        // Elementos decorativos no background
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = (-30).dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 280.dp, y = 80.dp)
                .background(
                    Color.White.copy(alpha = 0.08f),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar transparente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            }

            // Loading state
            if (carregandoDados) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Carregando grupo...",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp
                        )
                    }
                }
                return@Column
            }

            // Error state
            if (erroMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("😕", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                erroMsg!!,
                                color = Color(0xFFEF4444),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                return@Column
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Imagem do grupo em destaque
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Sombra/glow atrás da imagem
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .blur(30.dp)
                        .background(
                            AccentPurple.copy(alpha = 0.5f),
                            CircleShape
                        )
                )

                // Imagem principal
                Card(
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(20.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(SoftPurple)
                    ) {
                        Image(
                            painter = if (imagem.isNotEmpty())
                                rememberAsyncImagePainter(imagem)
                            else
                                painterResource(id = R.drawable.logoclaro),
                            contentDescription = nome,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Badge de verificado (opcional - decorativo)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-20).dp, y = (-5).dp)
                        .size(36.dp)
                        .background(SuccessGreen, CircleShape)
                        .border(3.dp, CardWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nome e info básica
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nome,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge de membros
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$membros membros",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card principal com conteúdo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Handle decorativo
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftPurple, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            icon = Icons.Default.Person,
                            value = "$membros",
                            label = "Membros"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(PrimaryPurple.copy(alpha = 0.2f))
                        )
                        StatItem(
                            icon = Icons.Default.Star,
                            value = "4.8",
                            label = "Avaliação"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(PrimaryPurple.copy(alpha = 0.2f))
                        )
                        StatItem(
                            icon = Icons.Default.Info,
                            value = "Ativo",
                            label = "Status"
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Seção Sobre
                    Text(
                        text = "Sobre o grupo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = descricao,
                        fontSize = 15.sp,
                        color = TextGray,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Seção de tags (decorativo)
                    Text(
                        text = "Categorias",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TagChip("🎯 Aprendizado")
                        TagChip("👥 Comunidade")
                        TagChip("🚀 Progresso")
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Botão principal
                    AnimatedContent(
                        targetState = participando,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "button_animation"
                    ) { isParticipando ->
                        Button(
                            onClick = {
                                val idGrupo = grupo?.id_grupo ?: return@Button

                                Log.d("GRUPO", "Botão Participar clicado. idGrupo=$idGrupo idUsuario=$idUsuario")

                                scope.launch {
                                    carregando = true

                                    try {
                                        val (jaParticipa, criouGrupo) = withContext(Dispatchers.IO) {
                                            val participaList = RetrofitInstance.grupoService
                                                .listarGruposParticipando(idUsuario)
                                                .execute()
                                                .body()?.grupos.orEmpty()

                                            val criaList = RetrofitInstance.grupoService
                                                .listarGruposCriados(idUsuario)
                                                .execute()
                                                .body()?.grupos.orEmpty()

                                            Pair(
                                                participaList.any { it.id_grupo == idGrupo },
                                                criaList.any { it.id_grupo == idGrupo }
                                            )
                                        }

                                        if (jaParticipa || criouGrupo) {
                                            navController.navigate("home_grupo/$idGrupo/$idUsuario")
                                            return@launch
                                        }

                                        val resp = withContext(Dispatchers.IO) {
                                            RetrofitInstance.grupoService.participarDoGrupo(
                                                idGrupo,
                                                mapOf("id_usuario" to idUsuario)
                                            ).execute()
                                        }

                                        Log.d("GRUPO", "Resposta participar: code=${resp.code()} body=${resp.errorBody()?.string()}")

                                        if (resp.isSuccessful) {
                                            participando = true
                                            Toast.makeText(context, "Agora você participa do grupo!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("home_grupo/$idGrupo/$idUsuario")
                                        } else {
                                            Toast.makeText(context, "Erro ao participar", Toast.LENGTH_SHORT).show()
                                        }

                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        carregando = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .shadow(
                                    elevation = if (!isParticipando) 12.dp else 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = if (!isParticipando) PrimaryPurple else SuccessGreen,
                                    spotColor = if (!isParticipando) PrimaryPurple else SuccessGreen
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isParticipando) SuccessGreen else PrimaryPurple,
                                disabledContainerColor = PrimaryPurple.copy(alpha = 0.6f)
                            ),
                            enabled = !carregando
                        ) {
                            if (carregando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Text(
                                text = when {
                                    carregando -> "Entrando..."
                                    isParticipando -> "✓ Participando"
                                    else -> "Participar do Grupo"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Texto auxiliar
                    if (!participando) {
                        Text(
                            text = "Ao participar, você terá acesso a todas as atividades do grupo",
                            fontSize = 13.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryPurple,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGray
        )
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SoftPurple
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 13.sp,
            color = PrimaryPurpleDark,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewGrupoInfo() {
    val fakeNav = androidx.navigation.compose.rememberNavController()
    GrupoInfo(navController = fakeNav, grupoId = 1)
}