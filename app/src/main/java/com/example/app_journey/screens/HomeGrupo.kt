package com.example.app_journey.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Cores do tema
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF4A3CB5)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val BackgroundLight = Color(0xFFF8F9FE)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF6B7280)
private val CardWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGrupo(
    navController: NavHostController,
    grupoId: Int,
    idUsuario: Int
) {
    var grupo by remember { mutableStateOf<com.example.app_journey.model.Grupo?>(null) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(grupoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
            }
            if (response.isSuccessful) {
                grupo = response.body()?.grupos?.firstOrNull()
                    ?: run { erroMsg = "Grupo não encontrado"; null }
            } else erroMsg = "Erro: ${response.code()}"
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }

    val nome = grupo?.nome ?: "Grupo"
    val descricao = grupo?.descricao ?: "Sem descrição"
    val imagem = grupo?.imagem ?: ""
    val membros = grupo?.limite_membros ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header com gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryPurple, PrimaryPurpleDark)
                    )
                )
        )

        // Elementos decorativos
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-40).dp, y = (-20).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 300.dp, y = 60.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
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
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Menu de opções
                IconButton(
                    onClick = { /* menu */ },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = Color.White
                    )
                }
            }

            // Loading
            if (carregandoDados) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregando...", color = Color.White.copy(alpha = 0.9f))
                    }
                }
                return@Column
            }

            // Error
            if (erroMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("😕", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(erroMsg!!, color = Color(0xFFEF4444), textAlign = TextAlign.Center)
                        }
                    }
                }
                return@Column
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagem do grupo
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .blur(25.dp)
                        .background(AccentPurple.copy(alpha = 0.5f), CircleShape)
                )

                Card(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(16.dp, CircleShape),
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome e membros
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nome,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            modifier = Modifier.size(16.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Card principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 500.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Ações rápidas
                    Text(
                        text = "Acesso Rápido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Email,
                            title = "Chat",
                            subtitle = "Converse com o grupo",
                            gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
                            onClick = { navController.navigate("chat_grupo/${grupoId}") }
                        )

                        ActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.DateRange,
                            title = "Calendário",
                            subtitle = "Eventos e atividades",
                            gradientColors = listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
                            onClick = { navController.navigate("calendario/${grupo?.id_grupo}/${idUsuario}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Descrição
                    Text(
                        text = "Sobre o grupo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftPurple.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = descricao,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 15.sp,
                            color = TextGray,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Mais opções
                    Text(
                        text = "Mais opções",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MenuOption(
                        icon = Icons.Default.Person,
                        title = "Membros",
                        subtitle = "Ver todos os participantes",
                        onClick = { /* navegar para membros */ }
                    )

                    MenuOption(
                        icon = Icons.Default.Settings,
                        title = "Configurações",
                        subtitle = "Gerenciar grupo",
                        onClick = { /* navegar para config */ }
                    )

                    MenuOption(
                        icon = Icons.Default.Share,
                        title = "Compartilhar",
                        subtitle = "Convidar pessoas",
                        onClick = { /* compartilhar */ }
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SoftPurple, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextGray
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGray
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewHomeGrupo() {
    val fakeNav = rememberNavController()
    HomeGrupo(navController = fakeNav, idUsuario = 1, grupoId = 1)
}