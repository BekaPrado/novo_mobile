package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.PurpleDarker
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//------------------------------------------------------
// TELA DE PERFIL (DESIGN SEU + FUNCIONALIDADES REAIS)
//------------------------------------------------------
@Composable
fun Perfil(navController: NavHostController) {

    val usuarioLogado = remember { mutableStateOf<Usuario?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    //  BUSCAR USUÁRIO AO ABRIR A TELA
    LaunchedEffect(Unit) {
        if (idUsuario != -1) {

            RetrofitFactory()
                .getUsuarioService()
                .getUsuarioPorId(idUsuario)
                .enqueue(object : Callback<UsuarioResult> {

                    override fun onResponse(
                        call: Call<UsuarioResult>,
                        response: Response<UsuarioResult>
                    ) {
                        if (response.isSuccessful) {

                            usuarioLogado.value =
                                response.body()?.usuario?.firstOrNull()

                            loading.value = false

                        } else {
                            errorMessage.value = "Erro ao carregar usuário"
                            loading.value = false
                        }
                    }

                    override fun onFailure(call: Call<UsuarioResult>, t: Throwable) {
                        errorMessage.value = "Erro: ${t.message}"
                        loading.value = false
                    }
                })

        } else {
            errorMessage.value = "Usuário inválido"
            loading.value = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        //------------------------------------------------------
        // TOPO ROXO — SEU DESIGN
        //------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryPurple, PurpleDarker)
                    )
                )
        ) {

            // BOTÃO VOLTAR
            IconButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(10.dp))

                // FOTO OU ÍCONE
                if (usuarioLogado.value?.foto_perfil != null) {
                    Image(
                        painter = rememberAsyncImagePainter(usuarioLogado.value?.foto_perfil),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(100.dp))
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(110.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    usuarioLogado.value?.nome_completo ?: "Carregando...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    usuarioLogado.value?.email ?: "",
                    color = Color.White.copy(.8f),
                    fontSize = 13.sp
                )
            }
        }

        //------------------------------------------------------
        // CURVATURA BRANCA
        //------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                )
        )

        //------------------------------------------------------
        // CONTEÚDO
        //------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            when {
                loading.value -> {
                    CircularProgressIndicator(color = PrimaryPurple)
                }

                errorMessage.value != null -> {
                    Text(
                        errorMessage.value!!,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                usuarioLogado.value != null -> {
                    val usuario = usuarioLogado.value!!

                    CardInformacoes(usuario) {
                        navController.navigate("editar_info/$idUsuario")
                    }

                    Spacer(Modifier.height(22.dp))

                    CardBiografia(usuario.descricao ?: "") {
                        navController.navigate("editar_info/$idUsuario")
                    }
                }
            }
        }
    }
}

//------------------------------------------------------
// CARD DE INFORMAÇÕES
//------------------------------------------------------
@Composable
fun CardInformacoes(usuario: Usuario, onEditClick: () -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Informações pessoais",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )

                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = PrimaryPurple
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            InfoRow("Nome completo", usuario.nome_completo)
            InfoRow("Email", usuario.email)
        }
    }
}

//------------------------------------------------------
// CARD BIOGRAFIA
//------------------------------------------------------
@Composable
fun CardBiografia(descricao: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Biografia",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )

                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryPurple)
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = descricao.ifBlank { "Nenhuma biografia cadastrada." },
                fontSize = 14.sp
            )
        }
    }
}

//------------------------------------------------------
// INFO ROW
//------------------------------------------------------
@Composable
fun InfoRow(label: String, value: String?) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value ?: "", fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

//------------------------------------------------------
// PREVIEW
//------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewPerfil() {

    val usuarioMock = Usuario(
        id_usuario = 1,
        nome_completo = "Gabriel Guedes",
        email = "guedes@example.com",
        data_nascimento = null,
        foto_perfil = null,
        descricao = "Desenvolvedor Mobile • Apaixonado por tecnologia",
        senha = "",
        tipo_usuario = ""
    )

    val navController = rememberNavController()

    Column(Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryPurple, PurpleDarker)
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        ) {

            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(110.dp)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    usuarioMock.nome_completo ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    usuarioMock.email ?: "",
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
        )

        Column(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            CardInformacoes(usuarioMock) {}
            Spacer(Modifier.height(20.dp))
            CardBiografia(usuarioMock.descricao ?: "") {}
        }
    }
}
