package com.example.app_journey.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_journey.model.Grupo
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
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
private val AccentOrange = Color(0xFFFF9F43)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentPink = Color(0xFFEC4899)
private val AccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeusGrupos(navController: NavHostController) {
    val context = LocalContext.current
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var grupos by remember { mutableStateOf<List<Grupo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var filtroSelecionado by remember { mutableStateOf("Todos") }

    val scope = rememberCoroutineScope()

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "meusgrupos")
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

    // Carregar grupos
    LaunchedEffect(Unit) {
        scope.launch {
            loading = true
            try {
                val responseCriados = withContext(Dispatchers.IO) {
                    RetrofitInstance.grupoService.listarGruposCriados(idUsuario).execute()
                }
                val gruposCriados = if (responseCriados.isSuccessful)
                    responseCriados.body()?.grupos ?: emptyList()
                else
                    emptyList()

                val responseParticipando = withContext(Dispatchers.IO) {
                    RetrofitInstance.grupoService.listarGruposParticipando(idUsuario).execute()
                }
                val gruposParticipando = if (responseParticipando.isSuccessful)
                    responseParticipando.body()?.grupos ?: emptyList()
                else
                    emptyList()

                grupos = (gruposCriados + gruposParticipando).distinctBy { it.id_grupo }
                errorMessage = null
            } catch (e: Exception) {
                Log.e("MeusGrupos", "Erro ao carregar grupos", e)
                errorMessage = "Erro: ${e.localizedMessage ?: "desconhecido"}"
            } finally {
                loading = false
            }
        }
    }

    // Filtro de busca
    val gruposFiltrados = grupos.filter {
        it.nome.contains(searchText, ignoreCase = true)
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
                .height(260.dp)
        ) {
            // Gradiente de fundo
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
            MeusGruposFloatingElements(bounce, glowAnim)

            // Botão voltar
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 8.dp)
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
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Conteúdo do header
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícone principal
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Meus Grupos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        }

        // =============== CONTEÚDO PRINCIPAL ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Spacer(modifier = Modifier.height(220.dp))

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
                        placeholder = { Text("Buscar grupo...", color = TextMuted) },
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

                    // =============== FILTROS ===============
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Todos", "Criados", "Participando").forEach { filtro ->
                            val isSelected = filtroSelecionado == filtro
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) PrimaryPurple else SoftPurple,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { filtroSelecionado = filtro }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = filtro,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else PrimaryPurple
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== LISTA DE GRUPOS ===============
                    when {
                        loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = PrimaryPurple,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Carregando grupos...",
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        errorMessage != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "😕", fontSize = 60.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = errorMessage ?: "Erro desconhecido",
                                        color = AccentRed,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { /* retry */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                                    ) {
                                        Text("Tentar novamente")
                                    }
                                }
                            }
                        }

                        gruposFiltrados.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(40.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(SoftPurple, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "📁", fontSize = 48.sp)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        "Nenhum grupo encontrado",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Você ainda não participa de nenhum grupo.\nQue tal criar ou participar de um?",
                                        fontSize = 14.sp,
                                        color = TextGray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        lineHeight = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { navController.navigate("criar_grupo") },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Criar Grupo",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(gruposFiltrados) { index, grupo ->
                                    ModernGrupoItem(
                                        grupo = grupo,
                                        index = index,
                                        onClick = {
                                            navController.navigate("grupoinfo/${grupo.id_grupo}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =============== FAB CRIAR GRUPO ===============
        if (!loading && grupos.isNotEmpty()) {
            FloatingActionButton(
                onClick = { navController.navigate("criar_grupo") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .shadow(12.dp, CircleShape),
                containerColor = PrimaryPurple,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Criar Grupo",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun MeusGruposFloatingElements(bounce: Float, glow: Float) {
    // Círculos decorativos
    Box(
        modifier = Modifier
            .offset(x = 280.dp, y = (30 + bounce * 0.5f).dp)
            .size(100.dp)
            .blur(40.dp)
            .background(Color.White.copy(alpha = glow * 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-40).dp, y = (80 + bounce).dp)
            .size(80.dp)
            .blur(30.dp)
            .background(Color.White.copy(alpha = glow * 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 320.dp, y = (180 + bounce * 0.7f).dp)
            .size(60.dp)
            .blur(25.dp)
            .background(AccentPurple.copy(alpha = glow * 0.15f), CircleShape)
    )

    // Estrelinhas
    listOf(
        Triple(60.dp, 40.dp, 4.dp),
        Triple(300.dp, 100.dp, 3.dp),
        Triple(40.dp, 160.dp, 3.dp),
        Triple(260.dp, 200.dp, 4.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.5f), CircleShape)
        )
    }
}

// =============== CARD DE GRUPO MODERNO ===============
@Composable
private fun ModernGrupoItem(
    grupo: Grupo,
    index: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val colors = listOf(AccentPurple, AccentBlue, AccentPink, AccentGreen, AccentOrange)
    val cardColor = colors[index % colors.size]

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300, delayMillis = index * 60)) +
                slideInHorizontally(tween(300, delayMillis = index * 60)) { it / 3 }
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
                // Imagem do grupo
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (!grupo.imagem.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(grupo.imagem)
                                .crossfade(true)
                                .build(),
                            contentDescription = grupo.nome,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(cardColor, cardColor.copy(alpha = 0.7f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grupo.nome.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Badge de status
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(18.dp)
                            .background(AccentGreen, CircleShape)
                            .border(2.dp, CardWhite, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Informações do grupo
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = grupo.nome,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = grupo.descricao ?: "Sem descrição",
                        fontSize = 13.sp,
                        color = TextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Membros
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(SoftPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${grupo.limite_membros}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryPurple
                            )
                        }

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(AccentOrange.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "4.${5 + (index % 5)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AccentOrange
                            )
                        }
                    }
                }

                // Seta
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SoftPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}