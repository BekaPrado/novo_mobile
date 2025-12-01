package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Area
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val RoxoPrimario = Color(0xFF4A39C7)
private val RoxoEscuro = Color(0xFF341E9B)
private val CinzaCard = Color(0xFFF4F5F8)

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

    var areaNome by remember { mutableStateOf("Carregando área...") }
    var responsavelNome by remember { mutableStateOf("Carregando responsável...") }

    var participando by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    // ======================
    // CARREGAMENTO DO GRUPO
    // ======================
    LaunchedEffect(grupoId) {

        try {
            withContext(Dispatchers.IO) {

                val response =
                    RetrofitInstance.grupoService.getGrupoById(grupoId).execute()

                if (response.isSuccessful) {

                    grupo = response.body()?.grupo


                    grupo?.let {

                        // =====================
                        // BUSCA ÁREA
                        // =====================
                        val areas =
                            RetrofitInstance.areaService
                                .listarAreas()
                                .execute()
                                .body()
                                ?.areas

                        val area = areas?.firstOrNull {
                            it.id_area == grupo!!.id_area
                        }

                        areaNome = area?.area ?: "Área não encontrada"

                        // =====================
                        // BUSCA RESPONSÁVEL
                        // =====================
                        val usuarioResp =
                            RetrofitInstance.usuarioService
                                .getUsuarioPorIdSuspend(grupo!!.id_usuario)

                        responsavelNome =
                            usuarioResp.usuario
                                ?.firstOrNull()
                                ?.nome_completo
                                ?: "Responsável não encontrado"

                    }

                } else erroMsg = "Erro ao carregar grupo: ${response.code()}"
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

    // ======================
    // TELA
    // ======================

    Scaffold(
        topBar = {

            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = RoxoEscuro
                        )
                    }
                }
            )

        },

        bottomBar = {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (participando) Color(0xFF4CAF50)
                        else RoxoPrimario
                ),

                enabled = !carregando,

                onClick = {

                    val idGrupo = grupo?.id_grupo ?: return@Button

                    scope.launch {

                        carregando = true

                        try {

                            val (jaParticipa, criouGrupo) =
                                withContext(Dispatchers.IO) {

                                    val participaList =
                                        RetrofitInstance.grupoService
                                            .listarGruposParticipando(idUsuario)
                                            .execute()
                                            .body()?.grupos ?: emptyList()

                                    val criaList =
                                        RetrofitInstance.grupoService
                                            .listarGruposCriados(idUsuario)
                                            .execute()
                                            .body()?.grupos ?: emptyList()

                                    Pair(
                                        participaList.any { it.id_grupo == idGrupo },
                                        criaList.any { it.id_grupo == idGrupo }
                                    )
                                }

                            if (jaParticipa || criouGrupo) {
                                navController.navigate("home_grupo/$grupoId/$idUsuario")
                                return@launch
                            }

                            val resp = withContext(Dispatchers.IO) {
                                RetrofitInstance.grupoService.participarDoGrupo(
                                    idGrupo,
                                    mapOf("id_usuario" to idUsuario)
                                ).execute()
                            }

                            if (resp.isSuccessful) {
                                participando = true

                                Toast.makeText(
                                    context,
                                    "Agora você participa do grupo!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate(
                                    "home_grupo/$grupoId/$idUsuario"
                                )

                            } else Toast.makeText(
                                context,
                                "Erro ao participar",
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                "Erro: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()

                        } finally {
                            carregando = false
                        }
                    }
                }
            ) {

                Text(
                    when {
                        carregando -> "Entrando..."
                        participando -> "Participando"
                        else -> "Participar"
                    },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }

    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            if (carregandoDados) {

                Spacer(Modifier.height(40.dp))
                CircularProgressIndicator(
                    Modifier.align(Alignment.CenterHorizontally)
                )
                return@Column
            }

            erroMsg?.let {

                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
                return@Column
            }

            // ======================
            // IMAGEM GRANDE
            // ======================
            Image(
                painter =
                    if (imagem.isNotEmpty())
                        rememberAsyncImagePainter(imagem)
                    else painterResource(id = R.drawable.logoclaro),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                contentDescription = nome,
                contentScale = ContentScale.Crop
            )

            // ======================
            // CARD PRINCIPAL
            // ======================
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

                    Spacer(Modifier.height(10.dp))

                    Divider()

                    Spacer(Modifier.height(10.dp))

                    // ======================
                    // INFO EXTRA
                    // ======================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        InfoLinha("Área", areaNome)

                        InfoLinha("Responsável", responsavelNome)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Descrição",
                        fontWeight = FontWeight.SemiBold,
                        color = RoxoEscuro
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        descricao,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoLinha(titulo: String, valor: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            titulo,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Text(
            valor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
@Composable
private fun GrupoInfoPreviewContent(
    navController: NavHostController,
    grupo: com.example.app_journey.model.Grupo,
    areaNome: String,
    responsavelNome: String
) {

    val nome = grupo.nome
    val descricao = grupo.descricao
    val membros = grupo.limite_membros
    val imagem = grupo.imagem ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // IMAGEM GRANDE
        Image(
            painter = if (imagem.isNotEmpty())
                rememberAsyncImagePainter(imagem)
            else painterResource(id = R.drawable.logoclaro),

            contentScale = ContentScale.Crop,

            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),

            contentDescription = null
        )

        // CARD PRINCIPAL
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF4F5F8)
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(Modifier.padding(18.dp)) {

                Text(
                    nome,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "$membros membros",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(12.dp))

                Divider()

                Spacer(Modifier.height(12.dp))

                InfoLinha("Área", areaNome)

                InfoLinha("Responsável", responsavelNome)

                Spacer(Modifier.height(16.dp))

                Text(
                    "Descrição",
                    color = Color(0xFF341E9B),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    descricao,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A39C7)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        "Participar",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewGrupoInfo() {

    val fakeNav = androidx.navigation.compose.rememberNavController()

    // ==========================
    // MOCK DO GRUPO
    // ==========================
    val grupoMock = com.example.app_journey.model.Grupo(
        id_grupo = 1,
        nome = "Grupo de Desenvolvimento Mobile",
        limite_membros = 128,
        descricao = "Grupo focado em estudos de Kotlin, Jetpack Compose " +
                "e desenvolvimento de aplicativos Android para projetos " +
                "pessoais, acadêmicos e profissionais.",
        imagem = "",
        id_area = 2,
        id_usuario = 5
    )

    // ==========================
    // MOCK DA ÁREA
    // ==========================
    val areaMock = "Tecnologia da Informação"

    // ==========================
    // MOCK DO RESPONSÁVEL
    // ==========================
    val responsavelMock = "Gabriel Silva"

    // ==========================
    // PREVIEW REAL DA UI
    // ==========================

    GrupoInfoPreviewContent(
        navController = fakeNav,
        grupo = grupoMock,
        areaNome = areaMock,
        responsavelNome = responsavelMock
    )

}

