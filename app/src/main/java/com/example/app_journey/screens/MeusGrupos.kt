package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Grupo
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeusGrupos(navController: NavHostController) {
    val context = LocalContext.current
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var grupos by remember { mutableStateOf<List<Grupo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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


                errorMessage = if (grupos.isEmpty()) "Você ainda não participa de nenhum grupo." else null
            } catch (e: Exception) {
                Log.e("MeusGrupos", "Erro ao carregar grupos", e)
                errorMessage = "Erro: ${e.localizedMessage ?: "desconhecido"}"
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logoclaro),
                            contentDescription = "Logo Journey",
                            modifier = Modifier.height(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Meus Grupos",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF351D9B))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F2FF))
        ) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                errorMessage != null -> Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(grupos) { grupo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clickable {
                                    // ROTA CORRETA (tudo minúsculo)
                                    navController.navigate("grupoinfo/${grupo.id_grupo}")
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE0E0FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!grupo.imagem.isNullOrBlank()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(grupo.imagem),
                                            contentDescription = grupo.nome,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = grupo.nome.firstOrNull()?.toString() ?: "?",
                                            fontSize = 28.sp,
                                            color = Color(0xFF351D9B),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        grupo.nome,
                                        color = Color(0xFF351D9B),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${grupo.limite_membros} membros",
                                        color = Color(0xFF351D9B).copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
