package com.example.app_journey.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

import com.example.app_journey.R
import com.example.app_journey.model.Area
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ============================
// CORES DO PADRÃO VISUAL
// ============================
private val RoxoPrimario = Color(0xFF4A39C7)
private val RoxoEscuro = Color(0xFF341E9B)
private val CinzaCard = Color(0xFFF4F5F8)

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

    // ============================
    // BUSCA DO GRUPO
    // ============================
    LaunchedEffect(grupoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
            }
            if (response.isSuccessful) {
                grupo = response.body()?.grupo
                    ?: run { erroMsg = "Grupo não encontrado"; null }
            } else {
                erroMsg = "Erro: ${response.code()}"
            }
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }

    val nome = grupo?.nome ?: ""
    val descricao = grupo?.descricao ?: ""
    val imagem = grupo?.imagem ?: ""
    val membros = grupo?.limite_membros ?: 0

    // ============================
    // TELA
    // ============================
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = RoxoEscuro
                        )
                    }
                }
            )
        }
    ) { padding ->

        // BACKGROUND DA TELA
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .paint(
                    painterResource(id = R.drawable.background),
                    contentScale = ContentScale.Crop
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                // ============================
                // FEEDBACK DE ESTADO
                // ============================
                if (carregandoDados) {
                    Spacer(Modifier.height(40.dp))
                    CircularProgressIndicator(
                        Modifier.align(Alignment.CenterHorizontally)
                    )
                    return@Column
                }

                if (erroMsg != null) {
                    Text(
                        text = erroMsg!!,
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                    return@Column
                }

                // ============================
                // IMAGEM GRANDE TOPO
                // ============================
                Image(
                    painter =
                        if (imagem.isNotEmpty())
                            rememberAsyncImagePainter(imagem)
                        else painterResource(id = R.drawable.logoclaro),

                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .graphicsLayer {
                            alpha = 0.92f
                        },

                    contentDescription = nome
                )


                // ============================
                // CARD PRINCIPAL
                // ============================
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CinzaCard
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(Modifier.padding(18.dp)) {

                        Text(
                            nome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "$membros membros",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Divider()

                        Spacer(Modifier.height(12.dp))

                        // ============================
                        // DESCRIÇÃO
                        // ============================
                        Text(
                            "Descrição do grupo",
                            fontWeight = FontWeight.SemiBold,
                            color = RoxoEscuro
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            descricao,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        // ============================
                        // BOTÕES (FUNCIONALIDADES)
                        // ============================
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Button(
                                onClick = {
                                    navController.navigate("chat_grupo/$grupoId")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RoxoPrimario
                                )
                            ) {
                                Text(
                                    "Chat",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    navController.navigate(
                                        "calendario/${grupo?.id_grupo}/$idUsuario"
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RoxoPrimario
                                )
                            ) {
                                Text(
                                    "Calendário",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeGrupo_Completo() {

    val fakeNav = rememberNavController()

    // ============================
    // MOCKS USANDO SEUS MODELS
    // ============================

    val areaMock = Area(
        id_area = 1,
        area = "Tecnologia da Informação"
    )

    val responsavelMock = Usuario(
        id_usuario = 10,
        nome_completo = "Gabriel Silva",
        email = "gabriel@email.com",
        data_nascimento = "2001-10-01",
        foto_perfil = null,
        descricao = "Dev Mobile Kotlin",
        senha = "123456",
        tipo_usuario = "ADMIN"
    )

    val grupoMock = Grupo(
        id_grupo = 1,
        nome = "Grupo de Desenvolvimento Mobile",
        limite_membros = 128,
        descricao = "Grupo focado em Kotlin, Jetpack Compose e desenvolvimento Android. " +
                "Aqui discutimos projetos, estudos, desafios técnicos e boas práticas " +
                "para evoluir no mobile.",
        imagem = "",   // deixa vazio pra usar logo padrão
        id_area = areaMock.id_area,
        id_usuario = responsavelMock.id_usuario ?: 10
    )

    // ============================
    // PREVIEW SEM RETROFIT
    // ============================

    Scaffold {

        Box(
            Modifier
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                // Banner
                Image(
                    painter = painterResource(id = R.drawable.logoclaro),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Imagem Grupo"
                )

                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F8))
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Text(
                            grupoMock.nome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "${grupoMock.limite_membros} membros",
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(12.dp))

                        Divider()

                        Spacer(Modifier.height(12.dp))

                        InfoPreview("Área", areaMock.area)
                        InfoPreview("Responsável", responsavelMock.nome_completo)

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = "Descrição",
                            color = Color(0xFF341E9B),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            grupoMock.descricao,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(22.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4A39C7)
                                ),
                                onClick = {}
                            ) {
                                Text(
                                    "Chat",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4A39C7)
                                ),
                                onClick = {}
                            ) {
                                Text(
                                    "Calendário",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPreview(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(titulo, color = Color.Gray, fontSize = 14.sp)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

