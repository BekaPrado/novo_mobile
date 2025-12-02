package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.TopAppBar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGrupo(
    navController: NavHostController,
    grupoId: Int,
    idUsuario: Int
) {
    var grupo by remember { mutableStateOf<com.example.app_journey.model.Grupo?>(null) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(grupoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
            }
            if (response.isSuccessful) {
                val wrapper = response.body()
                grupo = response.body()?.grupos?.firstOrNull()
                    ?: run { erroMsg = "Grupo não encontrado"; null }

            } else erroMsg = "Erro: ${response.code()}"
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }

    val nome = grupo?.nome ?: "Grupo"
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
                Spacer(modifier = Modifier.height(50.dp))
                CircularProgressIndicator()
                return@Column
            }

            if (erroMsg != null) {
                Text(text = erroMsg!!, color = Color.Red, fontSize = 18.sp)
                return@Column
            }

            // Card Principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2FF)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = if (imagem.isNotEmpty()) rememberAsyncImagePainter(imagem)
                            else painterResource(id = R.drawable.logoclaro),
                            contentDescription = nome,
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    Color(0xFFE0DFFF),
                                    RoundedCornerShape(16.dp)
                                )
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$membros membros",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        "Descrição do grupo",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF341E9B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFDDD9FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = descricao, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // BOTÕES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate("chat_grupo/${grupoId}") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A39C7))
                        ) {
                            Text("Chat", color = Color.White, fontSize = 17.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                navController.navigate("calendario/${grupo?.id_grupo}/${idUsuario}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A39C7))
                        ) {
                            Text("Calendário", color = Color.White, fontSize = 17.sp)
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    val fakeNav = rememberNavController()
    HomeGrupo(navController = fakeNav, idUsuario = 1, grupoId = 1)
}