package com.example.app_journey.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrupoInfo(
    navController: NavHostController,
    grupoId: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var grupo by remember { mutableStateOf<com.example.app_journey.model.Grupo?>(null) }
    var participando by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(grupoId) {
        if (grupoId <= 0) {
            carregandoDados = false
            erroMsg = "Grupo inválido (ID $grupoId)"
            return@LaunchedEffect
        }

        try {
            withContext(Dispatchers.IO) {
                val response = RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
                if (response.isSuccessful) {
                    val wrapper = response.body()
                    grupo = response.body()?.grupos?.firstOrNull()

                } else {
                    erroMsg = "Erro ao carregar grupo: ${response.code()}"
                }
            }
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }

    val nome = grupo?.nome ?: "Grupo sem nome"
    val descricao = grupo?.descricao ?: "Sem descrição"
    val imagem = grupo?.imagem ?: ""
    val membros = grupo?.limite_membros ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nome, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF341E9B)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (carregandoDados) {
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator()
                return@Column
            }

            if (erroMsg != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(erroMsg!!, color = Color.Red, fontSize = 18.sp)
                return@Column
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2FF)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Image(
                            painter =
                                if (imagem.isNotEmpty()) rememberAsyncImagePainter(imagem)
                                else painterResource(id = R.drawable.logoclaro),
                            contentDescription = nome,
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFFE0DFFF), RoundedCornerShape(16.dp))
                                .padding(6.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = nome,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E1E)
                            )
                            Text(
                                text = "$membros membros",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Descrição",
                        fontSize = 18.sp,
                        color = Color(0xFF341E9B),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFDDD9FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(descricao, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Botão participar
                    Button(
                        onClick = {
                            val idGrupo = grupo?.id_grupo ?: return@Button

                            Log.d("GRUPO", "Botão Participar clicado. idGrupo=$idGrupo idUsuario=$idUsuario")

                            scope.launch {
                                carregando = true

                                try {
                                    // Verifica participação
                                    val (jaParticipa, criouGrupo) = withContext(Dispatchers.IO) {
                                        val participaList = RetrofitInstance.grupoService
                                            .listarGruposParticipando(idUsuario)
                                            .execute()
                                            .body()?.grupos.orEmpty()

                                        val criaList = RetrofitInstance.grupoService
                                            .listarGruposCriados(idUsuario)
                                            .execute()
                                            .body()?.grupos.orEmpty()

                                        Pair(
                                            participaList.any { it.id_grupo == idGrupo },
                                            criaList.any { it.id_grupo == idGrupo }
                                        )
                                    }

                                    if (jaParticipa || criouGrupo) {
                                        navController.navigate("home_grupo/$idGrupo/$idUsuario")
                                        return@launch
                                    }

                                    // Chamada API
                                    val resp = withContext(Dispatchers.IO) {
                                        RetrofitInstance.grupoService.participarDoGrupo(
                                            idGrupo,
                                            mapOf("id_usuario" to idUsuario)
                                        ).execute()
                                    }

                                    Log.d("GRUPO", "Resposta participar: code=${resp.code()} body=${resp.errorBody()?.string()}")

                                    if (resp.isSuccessful) {
                                        participando = true
                                        Toast.makeText(context, "Agora você participa do grupo!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home_grupo/$idGrupo/$idUsuario")
                                    } else {
                                        Toast.makeText(context, "Erro ao participar", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    carregando = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (participando) Color(0xFF4CAF50) else Color(0xFF4A39C7)
                        ),
                        enabled = !carregando
                    ) {
                        Text(
                            text = when {
                                carregando -> "Entrando..."
                                participando -> "Participando"
                                else -> "Participar"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                }
            }
        }
    }
}



@Preview(showSystemUi = true)
@Composable
fun PreviewGrupoInfo() {
    val fakeNav = androidx.navigation.compose.rememberNavController()
    GrupoInfo(navController = fakeNav, grupoId = 1)
}