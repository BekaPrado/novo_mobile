package com.example.app_journey.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun CadastrarEbookScreen(
    onCancelar: () -> Unit,
    onPublicar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf(
        "📚 Ficção",
        "💕 Romance",
        "💻 Tecnologia",
        "📖 Educação",
        "💰 Finanças",
        "🎨 Design",
        "🧠 Autoajuda",
        "🔬 Ciências"
    )

    // Launchers para arquivos
    var uriCapa by remember { mutableStateOf<String?>(null) }
    var uriArquivo by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uriCapa = uri?.lastPathSegment }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uriArquivo = uri?.lastPathSegment }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "cadastrar")
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
        targetValue = 6f,
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

    // Validação simples
    val isFormValid = titulo.isNotBlank() && categoria.isNotBlank() && preco.isNotBlank()

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
            CadastrarFloatingElements(bounce, glowAnim)

            // Botão voltar
            IconButton(
                onClick = onCancelar,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
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

            // Título e ícone
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✍️", fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Publicar E-Book",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compartilhe seu conhecimento",
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
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // =============== SEÇÃO: INFORMAÇÕES BÁSICAS ===============
                    SectionTitle(icon = Icons.Default.Edit, title = "Informações Básicas")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título do e-book
                    ModernTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = "Título do e-book",
                        placeholder = "Ex: Guia Completo de Flutter",
                        icon = Icons.Default.Star
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categoria Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoria", color = TextGray) },
                            placeholder = { Text("Selecione uma categoria", color = TextMuted) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(SoftPurple, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            trailingIcon = {
                                Icon(
                                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = PrimaryPurple
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = SoftPurple,
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardWhite)
                        ) {
                            categorias.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            color = TextDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        categoria = option
                                        expanded = false
                                    },
                                    modifier = Modifier.background(
                                        if (categoria == option) SoftPurple else Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preço
                    ModernTextField(
                        value = preco,
                        onValueChange = { preco = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                        label = "Preço",
                        placeholder = "Ex: 29,90",
                        icon = Icons.Default.ShoppingCart,
                        prefix = "R$ "
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // =============== SEÇÃO: DESCRIÇÃO ===============
                    SectionTitle(icon = Icons.Default.Info, title = "Descrição")

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Sobre o e-book", color = TextGray) },
                        placeholder = { Text("Descreva o conteúdo do seu e-book...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SoftPurple,
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            cursorColor = PrimaryPurple
                        ),
                        maxLines = 5
                    )

                    Text(
                        text = "${descricao.length}/500 caracteres",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // =============== SEÇÃO: ARQUIVOS ===============
                    SectionTitle(icon = Icons.Default.Share, title = "Arquivos")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Upload da Capa
                    UploadCard(
                        title = "Capa do E-book",
                        subtitle = uriCapa ?: "PNG, JPG ou WEBP (máx. 5MB)",
                        icon = "🖼️",
                        isSelected = uriCapa != null,
                        onClick = { imagePickerLauncher.launch("image/*") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Upload do Arquivo
                    UploadCard(
                        title = "Arquivo do E-book",
                        subtitle = uriArquivo ?: "PDF ou EPUB (máx. 50MB)",
                        icon = "📄",
                        isSelected = uriArquivo != null,
                        onClick = { filePickerLauncher.launch("application/pdf") }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // =============== PREVIEW CARD ===============
                    if (titulo.isNotBlank() || categoria.isNotBlank()) {
                        Text(
                            text = "📱 Preview",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini capa
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(AccentPurple, AccentBlue)
                                            ),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📘", fontSize = 32.sp)
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = titulo.ifBlank { "Título do e-book" },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        maxLines = 2
                                    )

                                    if (categoria.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(SoftPurple, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = categoria,
                                                fontSize = 11.sp,
                                                color = PrimaryPurple
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = if (preco.isNotBlank()) "R$ $preco" else "R$ 0,00",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryPurple
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // =============== BOTÕES ===============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botão Cancelar
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, TextMuted)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cancelar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextGray
                            )
                        }

                        // Botão Publicar
                        Button(
                            onClick = onPublicar,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(
                                    if (isFormValid) 8.dp else 0.dp,
                                    RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) PrimaryPurple else TextMuted,
                                disabledContainerColor = TextMuted
                            ),
                            enabled = isFormValid
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Publicar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// =============== COMPONENTES AUXILIARES ===============

@Composable
private fun CadastrarFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (30 + bounce * 0.5f).dp)
            .size(80.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-30).dp, y = (80 + bounce).dp)
            .size(60.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 340.dp, y = (120 + bounce * 0.7f).dp)
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
    )

    listOf(
        Triple(70.dp, 40.dp, 5.dp),
        Triple(280.dp, 70.dp, 4.dp),
        Triple(160.dp, 100.dp, 3.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.6f), CircleShape)
        )
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SoftPurple, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    prefix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextGray) },
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SoftPurple, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        prefix = prefix?.let { { Text(it, color = PrimaryPurple, fontWeight = FontWeight.Bold) } },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPurple,
            unfocusedBorderColor = SoftPurple,
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite,
            cursorColor = PrimaryPurple
        ),
        singleLine = true
    )
}

@Composable
private fun UploadCard(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SoftPurple else CardWhite
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryPurple else SoftPurple
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if (isSelected) PrimaryPurple.copy(alpha = 0.2f) else SoftPurple,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isSelected) PrimaryPurple else TextMuted,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isSelected) AccentGreen else SoftPurple,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isSelected) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}