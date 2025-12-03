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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.AzureUploader
import com.google.gson.Gson
import kotlinx.coroutines.launch

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
fun EditarInfo(
    navController: NavController,
    usuario: Usuario,
    onSave: (Usuario) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf(usuario.nome_completo) }
    var email by remember { mutableStateOf(usuario.email) }
    var dataNascimento by remember { mutableStateOf(usuario.data_nascimento?.take(10) ?: "") }
    var descricao by remember { mutableStateOf(usuario.descricao ?: "") }
    var tipoUsuario by remember { mutableStateOf(usuario.tipo_usuario) }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemUrl by remember { mutableStateOf(usuario.foto_perfil ?: "") }
    var enviando by remember { mutableStateOf(false) }

    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "editar")
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

    // Launcher para pegar imagem
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagemUri = uri
        uri?.let {
            scope.launch {
                enviando = true
                val inputStream = context.contentResolver.openInputStream(it)
                val fileName = "foto_perfil_${System.currentTimeMillis()}.jpg"
                if (inputStream != null) {
                    val url = AzureUploader.uploadImageToAzure(inputStream, fileName)
                    if (url != null) {
                        imagemUrl = url
                        Toast.makeText(context, "Imagem atualizada!", Toast.LENGTH_SHORT).show()
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
                .height(280.dp)
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
            EditarFloatingElements(bounce, glowAnim)

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
            Text(
                text = "Editar Perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            )
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
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    // Texto "Toque para alterar"
                    Text(
                        text = "Toque na foto para alterar",
                        fontSize = 14.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // =============== FORMULÁRIO ===============
                    EditarFormField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = "Nome completo",
                        icon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditarFormField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditarFormField(
                        value = dataNascimento,
                        onValueChange = { dataNascimento = it.take(10).replace(Regex("[^0-9-]"), "") },
                        label = "Data de Nascimento (AAAA-MM-DD)",
                        icon = Icons.Default.DateRange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditarFormField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = "Descrição / Bio",
                        icon = Icons.Default.Edit,
                        singleLine = false,
                        minHeight = 100.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditarFormField(
                        value = tipoUsuario,
                        onValueChange = { tipoUsuario = it },
                        label = "Tipo de Usuário",
                        icon = Icons.Default.Star
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // =============== BOTÃO SALVAR ===============
                    Button(
                        onClick = {
                            val usuarioAtualizado = usuario.copy(
                                nome_completo = nome,
                                email = email,
                                data_nascimento = dataNascimento,
                                descricao = descricao,
                                tipo_usuario = tipoUsuario,
                                foto_perfil = imagemUrl.ifBlank { null }
                            )
                            onSave(usuarioAtualizado)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
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
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Salvar Alterações",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão cancelar
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
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

        // =============== FOTO DO PERFIL (SOBREPOSTA) ===============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 110.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .blur(30.dp)
                    .background(PrimaryPurple.copy(alpha = glowAnim * 0.4f), CircleShape)
            )

            // Container da foto clicável
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(16.dp, CircleShape)
                    .background(CardWhite, CircleShape)
                    .border(
                        width = 4.dp,
                        brush = Brush.linearGradient(listOf(Color.White, SoftPurple)),
                        shape = CircleShape
                    )
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagemUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imagemUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Brush.linearGradient(listOf(PrimaryPurple, AccentPurple)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nome.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Ícone de câmera
            Box(
                modifier = Modifier
                    .offset(x = 45.dp, y = 45.dp)
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .background(PrimaryPurple, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Alterar foto",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
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
                            text = "Enviando imagem...",
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
private fun EditarFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (40 + bounce * 0.5f).dp)
            .size(70.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-20).dp, y = (100 + bounce).dp)
            .size(50.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    listOf(
        Triple(50.dp, 50.dp, 5.dp),
        Triple(280.dp, 80.dp, 4.dp),
        Triple(100.dp, 140.dp, 3.dp),
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
private fun EditarFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
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

// =============== WRAPPER ===============
@Composable
fun EditarInfoWrapper(navController: NavController, idUsuario: Int?) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idUsuario) {
        if (idUsuario != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idUsuario)
                usuario = result.usuario?.firstOrNull()
                if (usuario == null) errorMessage = "Usuário não encontrado"
            } catch (e: Exception) {
                errorMessage = "Erro ao carregar usuário"
            } finally {
                loading = false
            }
        } else {
            loading = false
            errorMessage = "ID inválido"
        }
    }

    when {
        loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundWhite),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        }
        usuario != null -> {
            EditarInfo(
                navController = navController,
                usuario = usuario!!,
                onSave = { usuarioAtualizado ->
                    val usuarioJson = Gson().toJson(usuarioAtualizado)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("usuarioAtualizado", usuarioJson)
                    navController.popBackStack()
                }
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundWhite),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "😕",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Erro desconhecido",
                        fontSize = 16.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

// =============== PREVIEW ===============
@Preview(showBackground = true)
@Composable
private fun PreviewEditarInfo() {
    val fakeNav = rememberNavController()
    val fakeUsuario = Usuario(
        id_usuario = 1,
        nome_completo = "Nicolas Lima",
        email = "nicolas@email.com",
        senha = "123456",
        data_nascimento = "2000-05-20",
        descricao = "Explorador do mundo e amante de tecnologia.",
        tipo_usuario = "Comum",
        foto_perfil = null
    )

    EditarInfo(
        navController = fakeNav,
        usuario = fakeUsuario,
        onSave = {}
    )
}