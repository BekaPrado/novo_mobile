package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.service.UsuarioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversasPrivadasScreen(
    navController: NavHostController,
    idUsuario: Int
) {
    var conversas by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(idUsuario) {
        scope.launch {
            try {
                Log.d("ConversasPrivadas", "Buscando conversas para o usuário: $idUsuario")

                // Busca a lista de usuários para conversas privadas
                val response = try {
                    withContext(Dispatchers.IO) {
                        RetrofitInstance.usuarioService.listarUsuarios().execute()
                    }
                } catch (e: Exception) {
                    Log.e("ConversasPrivadas", "Erro ao buscar usuários: ${e.message}")
                    null
                }

                if (response?.isSuccessful == true) {
                    response.body()?.let { result ->
                        if (result.status && result.usuario.isNotEmpty()) {
                            val listaUsuarios = result.usuario.filter { it.id_usuario != idUsuario }
                            Log.d("ConversasPrivadas", "${listaUsuarios.size} usuários carregados")
                            conversas = listaUsuarios
                        } else {
                            erro = "Nenhum usuário encontrado"
                            Log.e("ConversasPrivadas", "Resposta sem usuários")
                        }
                    } ?: run {
                        erro = "Resposta inválida do servidor"
                        Log.e("ConversasPrivadas", "Resposta nula")
                    }
                } else {
                    erro = "Erro ao carregar conversas: ${response?.code()}"
                    Log.e("ConversasPrivadas", "Erro na resposta: ${response?.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                erro = "Erro: ${e.message}"
            } finally {
                carregando = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Conversas Privadas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = Color(0xFFF2F2F7)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF2F2F7))
        ) {
            when {
                carregando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                erro != null -> {
                    Text(
                        text = erro ?: "Erro desconhecido",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                conversas.isEmpty() -> {
                    Text(
                        "Nenhuma conversa encontrada",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(conversas) { usuario: Usuario ->
                            ConversaItem(usuario = usuario) {
                                navController.navigate("chatPrivado/${usuario.id_usuario}/${usuario.nome_completo}/${idUsuario}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversaItem(usuario: Usuario, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (usuario.foto_perfil != null)
                    rememberAsyncImagePainter(usuario.foto_perfil)
                else
                    painterResource(id = R.drawable.logoclaro),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(usuario.nome_completo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(usuario.email ?: "", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}