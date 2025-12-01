package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

// --------------------- Paleta (roxos + verde banner) ---------------------
private val PrimaryPurple = Color(0xFF341E9B)
private val PrimaryPurpleLight = Color(0xFF5A3FFF)
private val SoftBackground = Color(0xFFF6F7FF)
private val BannerGreen = Color(0xFF2ECC71)
private val BannerGreenDark = Color(0xFF27A85A)
private val CardWhite = Color(0xFFFFFFFF)

// --------------------- Extension: clickBounce ---------------------
fun Modifier.clickBounce(onClick: () -> Unit): Modifier = composed {
    val anim = remember { androidx.compose.animation.core.Animatable(1f) }
    val scope = rememberCoroutineScope()

    this
        .graphicsLayer {
            scaleX = anim.value
            scaleY = anim.value
        }
        .clickable {
            scope.launch {
                anim.snapTo(0.92f)
                anim.animateTo(1f, animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing))
                onClick()
            }
        }
}

// --------------------- Home (tela completa, tudo em 1 arquivo) ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navegacao: NavHostController, idUsuario: Int) {
    val context = LocalContext.current

    // dados
    val grupos = remember { mutableStateListOf<Grupo>() }
    val areas = remember { mutableStateListOf<Area>() }

    var categoriaSelecionada by remember { mutableStateOf("Todas") }
    var search by remember { mutableStateOf("") }

    // carregar grupos
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

    // carregar áreas
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

    // filtro memoizado
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

    // animação header glow
    val infinite = rememberInfiniteTransition()
    val headerGlow by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Surface(modifier = Modifier.fillMaxSize(), color = SoftBackground) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ---------- Header curvo com search ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(PrimaryPurple, PrimaryPurpleLight)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .graphicsLayer { scaleX = headerGlow; scaleY = headerGlow }
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Journey", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Conexão e Conhecimento", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { /*fav*/ }) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                            }
                            IconButton(onClick = { /*cart*/ }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPurple) },
                        placeholder = { Text("Pesquisar grupos...", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            cursorColor = PrimaryPurple,
                            focusedBorderColor = Color(0xFFDADAF0),
                            unfocusedBorderColor = Color(0xFFECECF5)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------- Categorias como CARDS (dinâmicos via areas da API) ----------
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "Todas" card
                item {
                    CategoryCard(
                        title = "Todas",
                        selected = categoriaSelecionada == "Todas",
                        onClick = { categoriaSelecionada = "Todas" }
                    )
                }
                // cards dinamicos por area
                items(areas) { area ->
                    CategoryCard(
                        title = area.area,
                        selected = categoriaSelecionada == area.area,
                        onClick = { categoriaSelecionada = area.area }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- Conteúdo principal: lista de grupos ----------
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Grupos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                        items(gruposFiltrados) { grupo ->
                            AnimatedGrupoCard(
                                grupo = grupo,
                                onClick = { navegacao.navigate("grupoinfo/${grupo.id_grupo}") }
                            )
                        }

                        if (gruposFiltrados.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                                    Text("Nenhum grupo encontrado", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



// --------------------- Category Card (minimalista) ---------------------
@Composable
private fun CategoryCard(title: String, selected: Boolean, onClick: () -> Unit) {
    // animação de entrada
    val enterTransition = remember { Animatable(0f) }
    LaunchedEffect(Unit) { enterTransition.animateTo(1f, animationSpec = tween(380)) }

    val background = if (selected) PrimaryPurple else Color.White
    val contentColor = if (selected) Color.White else PrimaryPurple
    val elevation = if (selected) 10.dp else 2.dp

    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 90.dp)
            .clickBounce { onClick() }
            .graphicsLayer { alpha = enterTransition.value; translationY = (20 * (1 - enterTransition.value)) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            // ícone minimalista (círculo pequeno)
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(contentColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                // placeholder pequeno; manter ícone real se quiser
                Text(title.firstOrNull()?.toString() ?: "A", color = contentColor, fontWeight = FontWeight.Bold)
            }
            Text(title, color = contentColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 2)
        }
    }
}

// --------------------- Grupo Card animado ---------------------
@Composable
private fun AnimatedGrupoCard(grupo: Grupo, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 6 }),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp)
                .clickBounce(onClick),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                // imagem
                val ctx = LocalContext.current
                val imageRequest = remember(grupo.imagem) {
                    ImageRequest.Builder(ctx)
                        .data(grupo.imagem)
                        .crossfade(true)
                        .build()
                }

                AsyncImage(
                    model = imageRequest,
                    contentDescription = "Imagem do grupo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(grupo.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(grupo.descricao, color = Color.Gray, fontSize = 13.sp, maxLines = 2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Limite: ${grupo.limite_membros} membros", color = PrimaryPurple, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

            }
        }
    }
}

// --------------------- Preview ---------------------
@Preview(showBackground = true)
@Composable
private fun HomePrev() {
    val fakeNav = rememberNavController()
    Home(navegacao = fakeNav, idUsuario = 1)
}