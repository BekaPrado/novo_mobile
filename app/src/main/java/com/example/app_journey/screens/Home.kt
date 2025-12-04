package com.example.app_journey.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_journey.model.Area
import com.example.app_journey.model.AreaResult
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.service.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// =============== PALETA DE CORES MODERNA ===============
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
private val AccentOrange = Color(0xFFFF9F43)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentPink = Color(0xFFEC4899)

// =============== EXTENSION: CLICK BOUNCE ===============
fun Modifier.clickBounce(onClick: () -> Unit): Modifier = composed {
    val anim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this
        .graphicsLayer {
            scaleX = anim.value
            scaleY = anim.value
        }
        .clickable {
            scope.launch {
                anim.snapTo(0.95f)
                anim.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f))
                onClick()
            }
        }
}

// =============== HOME SCREEN ===============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navegacao: NavHostController, idUsuario: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val grupos = remember { mutableStateListOf<Grupo>() }
    val areas = remember { mutableStateListOf<Area>() }

    var categoriaSelecionada by remember { mutableStateOf("Todas") }
    var search by remember { mutableStateOf("") }

    // Carregar grupos
    LaunchedEffect(Unit) {
        RetrofitFactory().getGrupoService().listarGrupos()
            .enqueue(object : Callback<GruposResult> {
                override fun onResponse(call: Call<GruposResult>, response: Response<GruposResult>) {
                    if (response.isSuccessful) {
                        response.body()?.grupos?.let {
                            grupos.clear()
                            grupos.addAll(it)
                        }
                    }
                }
                override fun onFailure(call: Call<GruposResult>, t: Throwable) {
                    Toast.makeText(context, "Erro ao carregar grupos", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Carregar áreas
    LaunchedEffect(Unit) {
        RetrofitFactory().getAreaService().listarAreas()
            .enqueue(object : Callback<AreaResult> {
                override fun onResponse(call: Call<AreaResult>, response: Response<AreaResult>) {
                    if (response.isSuccessful) {
                        response.body()?.areas?.let {
                            areas.clear()
                            areas.addAll(it)
                        }
                    }
                }
                override fun onFailure(call: Call<AreaResult>, t: Throwable) {
                    Toast.makeText(context, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Filtro
    val areaSelecionadaObj = areas.find { it.area == categoriaSelecionada }
    val gruposFiltrados by remember(grupos, categoriaSelecionada, search) {
        mutableStateOf(
            grupos.filter { grupo ->
                val matchesCategoria = (categoriaSelecionada == "Todas") || (grupo.id_area == areaSelecionadaObj?.id_area)
                val matchesSearch = search.isBlank() || grupo.nome.contains(search, ignoreCase = true)
                matchesCategoria && matchesSearch
            }
        )
    }

    val gruposDestaque = grupos.take(3)

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "home")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // =============== DRAWER + CONTEÚDO ===============
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                idUsuario = idUsuario,
                onOptionSelected = { route ->
                    scope.launch { drawerState.close() }

                    when (route) {
                        "logout" -> {
                            context.getSharedPreferences("app_journey_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()
                            navegacao.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        "apagar_perfil" -> {
                            Toast.makeText(context, "Função em desenvolvimento", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            navegacao.navigate(route)
                        }
                    }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {
            // Background decorativo
            HomeBackgroundDecoration(glowAnim)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // =============== HEADER ===============
                HomeHeader(
                    idUsuario = idUsuario,
                    search = search,
                    onSearchChange = { search = it },
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = { /* notificações */ },
                    onProfileClick = { navegacao.navigate("perfil/$idUsuario") }
                )

                // =============== CONTEÚDO SCROLLÁVEL ===============
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Seção: Bem-vindo
                    item {
                        WelcomeSection(
                            onCriarGrupoClick = { navegacao.navigate("criar_grupo") }
                        )
                    }

                    // Seção: Categorias
                    item {
                        CategoriesSection(
                            areas = areas,
                            categoriaSelecionada = categoriaSelecionada,
                            onCategoriaClick = { categoriaSelecionada = it }
                        )
                    }

                    // Seção: Grupos em Destaque
                    if (gruposDestaque.isNotEmpty()) {
                        item {
                            FeaturedGroupsSection(
                                grupos = gruposDestaque,
                                onGrupoClick = { navegacao.navigate("grupoinfo/${it.id_grupo}") }
                            )
                        }
                    }

                    // Seção: Todos os Grupos
                    item {
                        Text(
                            text = "Explorar Grupos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                        )
                    }

                    // Lista de grupos
                    items(gruposFiltrados) { grupo ->
                        ModernGrupoCard(
                            grupo = grupo,
                            onClick = { navegacao.navigate("grupoinfo/${grupo.id_grupo}") }
                        )
                    }

                    // Estado vazio
                    if (gruposFiltrados.isEmpty()) {
                        item {
                            EmptyState()
                        }
                    }
                }
            }
        }
    }
}

// =============== HEADER ===============
@Composable
private fun HomeHeader(
    idUsuario: Int,
    search: String,
    onSearchChange: (String) -> Unit,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
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
            // Menu hamburger
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SoftPurple)
                        .clickable { onMenuClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Olá! 👋",
                        fontSize = 16.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Journey",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Notificações
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SoftPurple)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notificações",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(12.dp)
                            .background(AccentOrange, CircleShape)
                            .border(2.dp, CardWhite, CircleShape)
                    )
                }

                // Perfil
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryPurple, AccentPurple)
                            )
                        )
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search bar
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar grupos, temas...", color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpar", tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
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
    }
}

// =============== BACKGROUND DECORATION ===============
@Composable
private fun HomeBackgroundDecoration(glowIntensity: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    PrimaryPurple.copy(alpha = glowIntensity * 0.08f),
                    AccentPurple.copy(alpha = glowIntensity * 0.03f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = 400f
            ),
            center = Offset(size.width * 0.9f, size.height * 0.05f),
            radius = 400f
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentPurple.copy(alpha = glowIntensity * 0.06f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.1f, size.height * 0.9f),
                radius = 350f
            ),
            center = Offset(size.width * 0.1f, size.height * 0.9f),
            radius = 350f
        )
    }
}

// =============== WELCOME SECTION ===============
@Composable
private fun WelcomeSection(onCriarGrupoClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PrimaryPurple, AccentPurple)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comece sua\njornada hoje!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Conecte-se com grupos e aprenda junto",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onCriarGrupoClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Criar Grupo", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚀", fontSize = 40.sp)
                }
            }

            // Elementos decorativos
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-10).dp)
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-15).dp, y = 15.dp)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }
    }
}

// =============== CATEGORIES SECTION ===============
@Composable
private fun CategoriesSection(
    areas: List<Area>,
    categoriaSelecionada: String,
    onCategoriaClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Categorias", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(
                "Ver todas",
                fontSize = 14.sp,
                color = PrimaryPurple,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* ver todas */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ModernCategoryCard(
                    title = "Todas",
                    emoji = "🌟",
                    color = AccentOrange,
                    selected = categoriaSelecionada == "Todas",
                    onClick = { onCategoriaClick("Todas") }
                )
            }

            itemsIndexed(areas) { index, area ->
                val colors = listOf(AccentBlue, AccentGreen, AccentPink, PrimaryPurple, AccentOrange)
                val emojis = listOf("💻", "🎨", "📚", "🎯", "🔬", "💡", "🎮", "📊")

                ModernCategoryCard(
                    title = area.area,
                    emoji = emojis.getOrElse(index) { "📁" },
                    color = colors[index % colors.size],
                    selected = categoriaSelecionada == area.area,
                    onClick = { onCategoriaClick(area.area) }
                )
            }
        }
    }
}

// =============== MODERN CATEGORY CARD ===============
@Composable
private fun ModernCategoryCard(
    title: String,
    emoji: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        animScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f))
    }

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(130.dp)
            .graphicsLayer {
                scaleX = animScale.value
                scaleY = animScale.value
            }
            .clickBounce(onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color else CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 12.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (selected) Color.White.copy(alpha = 0.25f)
                        else color.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else TextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =============== FEATURED GROUPS SECTION ===============
@Composable
private fun FeaturedGroupsSection(
    grupos: List<Grupo>,
    onGrupoClick: (Grupo) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Em Destaque", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(AccentOrange, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🔥 Hot", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(grupos) { grupo ->
                FeaturedGrupoCard(grupo = grupo, onClick = { onGrupoClick(grupo) })
            }
        }
    }
}

// =============== FEATURED GRUPO CARD ===============
@Composable
private fun FeaturedGrupoCard(grupo: Grupo, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(180.dp)
            .clickBounce(onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(grupo.imagem)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Destaque", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                }

                Column {
                    Text(
                        text = grupo.nome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${grupo.limite_membros} membros",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

// =============== MODERN GRUPO CARD ===============
@Composable
private fun ModernGrupoCard(grupo: Grupo, onClick: () -> Unit) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clickBounce(onClick),
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
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(grupo.imagem)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(16.dp)
                            .background(AccentGreen, CircleShape)
                            .border(2.dp, CardWhite, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = grupo.nome,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = grupo.descricao,
                        fontSize = 13.sp,
                        color = TextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${grupo.limite_membros}", fontSize = 12.sp, color = PrimaryPurple, fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.8", fontSize = 12.sp, color = AccentOrange, fontWeight = FontWeight.Medium)
                        }
                    }
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

// =============== EMPTY STATE ===============
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🔍", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nenhum grupo encontrado", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tente buscar por outro termo", fontSize = 14.sp, color = TextGray)
    }
}

// =============== PREVIEW ===============
@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    Home(navegacao = rememberNavController(), idUsuario = 1)
}