package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Area
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.AzureUploader
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Outline

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

    // Carregar áreas
    LaunchedEffect(Unit) {
        RetrofitFactory().getAreaService().listarAreas()
            .enqueue(object : Callback<com.example.app_journey.model.AreaResult> {
                override fun onResponse(
                    call: Call<com.example.app_journey.model.AreaResult>,
                    response: Response<com.example.app_journey.model.AreaResult>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.areas?.let {
                            areas.clear()
                            areas.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<com.example.app_journey.model.AreaResult>, t: Throwable) {
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
                    } else {
                        Toast.makeText(context, "Falha no upload da imagem", Toast.LENGTH_SHORT).show()
                    }
                }
                enviando = false
            }
        }
    }

    val brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF4A33C3), Color(0xFF341E9B))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4FF))
    ) {

        // ===== ONDA ROXA =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(WaveShape())
                .background(brush)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            // Botão voltar
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navegacao.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "Criar novo grupo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF341E9B)
                    )

                    CampoRoxo("Nome do grupo", nome) { nome = it }

                    // Dropdown área
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { expandedArea = true },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                        ) {
                            Text(
                                areaSelecionada?.area ?: "Selecione a área",
                                color = Color(0xFF341E9B)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedArea,
                            onDismissRequest = { expandedArea = false }
                        ) {
                            areas.forEach { area ->
                                DropdownMenuItem(
                                    text = { Text(area.area) },
                                    onClick = {
                                        areaSelecionada = area
                                        id_area = area.id_area
                                        expandedArea = false
                                    }
                                )
                            }
                        }
                    }

                    CampoRoxo("Limite de membros", limite.filter { it.isDigit() }) {
                        limite = it
                    }

                    CampoRoxo("Descrição", descricao) { descricao = it }

                    // Seletor de imagem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFEDEBFF))
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {

                        if (imagemUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imagemUrl),
                                contentDescription = "Imagem do Grupo",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                "Selecionar imagem do grupo",
                                color = Color(0xFF5E4AE3),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = {

                            if (nome.isBlank() ||
                                id_area == null ||
                                limite.isBlank() ||
                                descricao.isBlank() ||
                                imagemUrl == null
                            ) {
                                mensagem = "Preencha todos os campos e envie uma imagem"
                                return@Button
                            }

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
                                        if (response.isSuccessful && response.body()?.status == true) {
                                            Toast.makeText(context, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                                            navegacao.navigate("home/$id_usuario")
                                        } else {
                                            mensagem = "Erro ao criar grupo"
                                        }
                                    }

                                    override fun onFailure(call: Call<GruposResult>, t: Throwable) {
                                        mensagem = "Erro: ${t.message}"
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5E4AE3)
                        )
                    ) {
                        Text(
                            "Criar Grupo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (mensagem.isNotEmpty()) {
                        Text(mensagem, color = Color.Red)
                    }
                }
            }
        }

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

/* CAMPO ROXO PADRÃO */
@Composable
fun CampoRoxo(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF5E4AE3),
            unfocusedBorderColor = Color(0xFFB8B3FF),
            focusedLabelColor = Color(0xFF341E9B),
            cursorColor = Color(0xFF341E9B)
        )
    )
}

/* ONDA DO TOPO */
class WaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            lineTo(0f, size.height * 0.75f)

            cubicTo(
                size.width * 0.25f, size.height,
                size.width * 0.75f, size.height * 0.5f,
                size.width, size.height * 0.75f
            )

            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCriarGrupo() {
    val nav = rememberNavController()
    CriarGrupo(nav)
}
