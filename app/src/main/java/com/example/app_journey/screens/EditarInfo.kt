package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.ui.theme.*
import com.example.app_journey.utils.AzureUploader
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    var senha by remember { mutableStateOf("") } // não mostra senha criptografada
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemUrl by remember { mutableStateOf(usuario.foto_perfil ?: "") }
    var enviando by remember { mutableStateOf(false) }

    // Launcher para pegar imagem da galeria
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

    val outlinedColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.Gray,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Editar Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar com borda e clique
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(PurpleLighter)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagemUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imagemUrl),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = nome.firstOrNull()?.toString() ?: "?",
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Toque para alterar a foto", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // Card principal com TextFields
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PurpleDarker),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome completo") },
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dataNascimento,
                        onValueChange = { dataNascimento = it.take(10).replace(Regex("[^0-9-]"), "") },
                        label = { Text("Data de Nascimento (AAAA-MM-DD)") },
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição") },
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tipoUsuario,
                        onValueChange = { tipoUsuario = it },
                        label = { Text("Tipo de Usuário") },
                        shape = RoundedCornerShape(50),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // salvar alterações
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Salvar alterações", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Overlay de carregamento
        if (enviando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}


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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage ?: "Erro desconhecido")
            }
        }
    }
}

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
        foto_perfil = "https://i.pravatar.cc/300" // imagem de teste
    )

    EditarInfo(
        navController = fakeNav,
        usuario = fakeUsuario,
        onSave = {}
    )
}
