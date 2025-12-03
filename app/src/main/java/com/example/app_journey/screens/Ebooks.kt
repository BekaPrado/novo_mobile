package com.example.app_journey.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.model.Ebook
import com.example.app_journey.service.EbookService

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
private val AccentOrange = Color(0xFFFF9F43)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentPink = Color(0xFFEC4899)
private val AccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEbooksScreen(
    ebookService: EbookService,
    onEbookClick: (Int) -> Unit,
    onCriarClick: () -> Unit,
    onCarrinhoClick: () -> Unit
) {
    var ebooks by remember { mutableStateOf(listOf<Ebook>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "ebooks")
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

    // Carregar ebooks
    LaunchedEffect(Unit) {
        try {
            val response = ebookService.getTodosEbooks()
            ebooks = response.ebooks ?: emptyList()
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    // Filtro
    val ebooksFiltrados = ebooks.filter { it.titulo.contains(searchText, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // =============== HEADER ===============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
            EbooksFloatingElements(bounce, glowAnim)

            // Ações do header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão criar
                IconButton(onClick = onCriarClick) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Criar E-book",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botão carrinho
                Box {
                    IconButton(onClick = onCarrinhoClick) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrinho",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Badge do carrinho
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(18.dp)
                            .background(AccentOrange, CircleShape)
                            .border(2.dp, PrimaryPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Título
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📚", fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "E-Books",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${ebooks.size} livros disponíveis",
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
            Spacer(modifier = Modifier.height(180.dp))

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
                        placeholder = { Text("Buscar e-book...", color = TextMuted) },
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

                    // =============== CATEGORIAS ===============
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Todos", "Finanças", "Dev", "Design").forEachIndexed { index, category ->
                            val isSelected = index == 0
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) PrimaryPurple else SoftPurple,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { /* filtrar */ }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else PrimaryPurple
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== CONTEÚDO ===============
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PrimaryPurple)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Carregando e-books...",
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
                                }
                            }
                        }

                        ebooksFiltrados.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "📖", fontSize = 60.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Nenhum e-book encontrado",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Tente buscar por outro título",
                                        fontSize = 14.sp,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(ebooksFiltrados) { index, ebook ->
                                    ModernEbookCard(
                                        ebook = ebook,
                                        index = index,
                                        onClick = { onEbookClick(ebook.id_ebooks) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =============== FAB CRIAR ===============
        FloatingActionButton(
            onClick = onCriarClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .shadow(12.dp, CircleShape),
            containerColor = PrimaryPurple,
            contentColor = Color.White
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Criar E-book",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun EbooksFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (40 + bounce * 0.5f).dp)
            .size(80.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-30).dp, y = (100 + bounce).dp)
            .size(60.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 340.dp, y = (150 + bounce * 0.7f).dp)
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
    )

    listOf(
        Triple(70.dp, 50.dp, 5.dp),
        Triple(280.dp, 90.dp, 4.dp),
        Triple(160.dp, 130.dp, 3.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.6f), CircleShape)
        )
    }
}

// =============== CARD DE EBOOK MODERNO ===============
@Composable
private fun ModernEbookCard(
    ebook: Ebook,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val colors = listOf(AccentPurple, AccentBlue, AccentPink, AccentGreen, AccentOrange)
    val coverColor = colors[index % colors.size]

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300, delayMillis = index * 80)) +
                scaleIn(tween(300, delayMillis = index * 80), initialScale = 0.8f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Capa do ebook
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(coverColor, coverColor.copy(alpha = 0.7f))
                            ),
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📘", fontSize = 48.sp)

                    if (index % 2 == 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(AccentOrange, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-20%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = ebook.titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R$ ${ebook.preco}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "4.${5 + (index % 5)}",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }
        }
    }
}