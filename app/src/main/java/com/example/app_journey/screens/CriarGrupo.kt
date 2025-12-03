package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextAlign
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
import com.example.app_journey.utils.AzureUploader
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
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
private val AccentRed = Color(0xFFEF4444)
private val DividerColor = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarGrupo(navegacao: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var limite by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var id_area by remember { mutableStateOf<Int?>(null) }
    var areaSelecionada by remember { mutableStateOf<Area?>(null) }
    var expandedArea by remember { mutableStateOf(false) }

    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemUrl by remember { mutableStateOf<String?>(null) }

    var mensagem by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    val id_usuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1
    val areas = remember { mutableStateListOf<Area>() }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "criar")
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
                    Toast.makeText(context, "Erro ao carregar áreas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Selecionar imagem
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagemUri = uri
        uri?.let {
            scope.launch {
                enviando = true
                val inputStream = context.contentResolver.openInputStream(it)
                val fileName = "grupo_${System.currentTimeMillis()}.jpg"
                if (inputStream != null) {
                    val url = AzureUploader.uploadImageToAzure(inputStream, fileName)
                    if (url != null) {
                        imagemUrl = url
                        Toast.makeText(context, "Imagem enviada!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Falha no upload", Toast.LENGTH_SHORT).show()
                    }
                }
                enviando = false
            }
        }
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
            CriarGrupoFloatingElements(bounce, glowAnim)

            // Botão voltar
            IconButton(
                onClick = { navegacao.popBackStack() },
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
                Text(
                    text = "Criar Grupo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Monte sua comunidade",
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // =============== SELETOR DE IMAGEM ===============
                    // =============== SELETOR DE IMAGEM ===============
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .shadow(8.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(SoftPurple)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        PrimaryPurple.copy(alpha = 0.3f),
                                        AccentPurple.copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagemUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imagemUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagem do grupo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay para indicar que pode trocar
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Trocar imagem",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            PrimaryPurple.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Adicionar imagem do grupo",
                                    color = PrimaryPurple,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Toque para selecionar",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // =============== FORMULÁRIO ===============
                    CriarGrupoFormField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = "Nome do grupo",
                        icon = Icons.Default.Create,
                        placeholder = "Ex: Desenvolvedores Android"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown de área
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Área/Categoria",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedArea,
                            onExpandedChange = { expandedArea = !expandedArea }
                        ) {
                            OutlinedTextField(
                                value = areaSelecionada?.area ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Selecione uma área", color = TextMuted) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .shadow(2.dp, RoundedCornerShape(14.dp)),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedBorderColor = DividerColor,
                                    focusedContainerColor = CardWhite,
                                    unfocusedContainerColor = CardWhite
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedArea,
                                onDismissRequest = { expandedArea = false }
                            ) {
                                areas.forEach { area ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                area.area,
                                                color = TextDark,
                                                fontWeight = if (areaSelecionada == area) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            areaSelecionada = area
                                            id_area = area.id_area
                                            expandedArea = false
                                        },
                                        leadingIcon = {
                                            if (areaSelecionada == area) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = AccentGreen
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CriarGrupoFormField(
                        value = limite,
                        onValueChange = { limite = it.filter { char -> char.isDigit() } },
                        label = "Limite de membros",
                        icon = Icons.Default.Person,
                        placeholder = "Max: 30"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CriarGrupoFormField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = "Descrição",
                        icon = Icons.Default.Edit,
                        placeholder = "Descreva o objetivo do grupo...",
                        singleLine = false,
                        minHeight = 120.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mensagem de erro
                    if (mensagem.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AccentRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AccentRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = mensagem,
                                    color = AccentRed,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // =============== BOTÃO CRIAR ===============
                    Button(
                        onClick = {
                            if (nome.isBlank() || id_area == null || limite.isBlank() ||
                                descricao.isBlank() || imagemUrl == null
                            ) {
                                mensagem = "Preencha todos os campos e envie uma imagem"
                                return@Button
                            }

                            mensagem = ""
                            enviando = true

                            val novoGrupo = Grupo(
                                id_grupo = 0,
                                nome = nome,
                                limite_membros = limite.toInt(),
                                descricao = descricao,
                                imagem = imagemUrl!!,
                                id_area = id_area!!,
                                id_usuario = id_usuario
                            )

                            RetrofitFactory().getGrupoService()
                                .inserirGrupo(novoGrupo)
                                .enqueue(object : Callback<GruposResult> {
                                    override fun onResponse(
                                        call: Call<GruposResult>,
                                        response: Response<GruposResult>
                                    ) {
                                        enviando = false
                                        if (response.isSuccessful && response.body()?.status == true) {
                                            Toast.makeText(
                                                context,
                                                "Grupo criado com sucesso! 🎉",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navegacao.navigate("home/$id_usuario") {
                                                popUpTo("criar_grupo") { inclusive = true }
                                            }
                                        } else {
                                            mensagem = "Erro ao criar grupo"
                                        }
                                    }

                                    override fun onFailure(call: Call<GruposResult>, t: Throwable) {
                                        enviando = false
                                        mensagem = "Erro de conexão"
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled = !enviando
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(PrimaryPurple, AccentPurple))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Criar Grupo",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão cancelar
                    OutlinedButton(
                        onClick = { navegacao.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, DividerColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
                    ) {
                        Text(
                            "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // =============== LOADING OVERLAY ===============
        if (enviando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (imagemUrl == null) "Enviando imagem..." else "Criando grupo...",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun CriarGrupoFloatingElements(bounce: Float, glow: Float) {
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

// =============== CAMPO DO FORMULÁRIO ===============
@Composable
private fun CriarGrupoFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = 56.dp
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted) },
            singleLine = singleLine,
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .shadow(2.dp, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                cursorColor = PrimaryPurple
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCriarGrupo() {
    CriarGrupo(navegacao = rememberNavController())
}