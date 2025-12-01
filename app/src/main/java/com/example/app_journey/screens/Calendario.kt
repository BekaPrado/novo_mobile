package com.example.app_journey.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Evento(
    val id: Int,
    val data: LocalDate,
    val nome: String,
    val descricao: String,
    val hora: String?,
    val link: String,
    val grupoId: Int
)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(
    navController: NavHostController,
    grupoId: Int,
    idUsuario: Int
) {
    val hoje = remember { LocalDate.now() }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(listOf<Evento>()) }

    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Campos do novo evento
    var novoNome by remember { mutableStateOf("") }
    var novaDescricao by remember { mutableStateOf("") }
    var novoLink by remember { mutableStateOf("") }
    var novaHora by remember { mutableStateOf("") }

    val context = LocalContext.current

    // ------------ BUSCA EVENTOS -----------------
    LaunchedEffect(Unit) {
        RetrofitInstance.calendarioService.getTodosEventos()
            .enqueue(object : Callback<CalendarioResponseWrapper> {
                override fun onResponse(
                    call: Call<CalendarioResponseWrapper>,
                    response: Response<CalendarioResponseWrapper>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.status == true && body.Calendario != null) {
                            eventos = body.Calendario.mapNotNull { item ->
                                try {
                                    val data = LocalDate.parse(item.data_evento.substring(0, 10))
                                    val hora = item.data_evento.substring(11, 16)

                                    Evento(
                                        id = item.id_calendario,
                                        data = data,
                                        nome = item.nome_evento,
                                        descricao = item.descricao,
                                        hora = hora,
                                        link = item.link,
                                        grupoId = item.id_grupo
                                    )
                                } catch (e: Exception) { null }
                            }.filter { it.grupoId == grupoId }
                        }
                    }
                }

                override fun onFailure(call: Call<CalendarioResponseWrapper>, t: Throwable) {}
            })
    }

    // ------------ ESTRUTURA DO CALENDÁRIO ----------
    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()

    val diasCalendario = buildList<LocalDate?> {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calendário",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )
                },
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
                .fillMaxSize()
                .background(Color(0xFFEDEEFF))
                .padding(16.dp)
        ) {

            // -------- Cabeçalho do Mês ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                    Text("‹")
                }

                Text(
                    text = "${mesAtual.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() }} ${mesAtual.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color(0xFF1E1E1E)
                )

                FilledTonalIconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                    Text("›")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------- Dias da Semana ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { dia ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dia,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF341E9B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------- Grade ----------
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(diasCalendario.size) { index ->
                    val dia = diasCalendario[index]

                    if (dia == null) {
                        Box(modifier = Modifier.aspectRatio(1f)) {}
                    } else {
                        val eventosDoDia = eventos.filter { it.data == dia }

                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    dataSelecionada = dia
                                    coroutineScope.launch { sheetState.show() }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (eventosDoDia.isNotEmpty())
                                    Color(0xFFDAD5FF)
                                else
                                    Color(0xFFF5F3FF)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dia.dayOfMonth.toString(),
                                    fontWeight = if (dia == hoje) FontWeight.Bold else FontWeight.Medium,
                                    color = Color(0xFF1E1E1E)
                                )

                                eventosDoDia.take(2).forEach { evento ->
                                    Text(
                                        evento.descricao,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                        color = Color(0xFF4A39C7)
                                    )
                                }

                                if (eventosDoDia.size > 2) {
                                    Text(
                                        "+${eventosDoDia.size - 2} mais",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -------- BOTTOM SHEET ------------------------
    if (dataSelecionada != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { dataSelecionada = null },
            containerColor = Color(0xFFF3F1FF),
            tonalElevation = 3.dp,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(5.dp)
                            .background(Color(0xFFB9B5E5), RoundedCornerShape(8.dp))
                    )
                }
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Eventos de ${dataSelecionada!!.dayOfMonth}/${dataSelecionada!!.monthValue}/${dataSelecionada!!.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF341E9B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val eventosDoDia = eventos.filter { it.data == dataSelecionada }

                // -------- LISTA DE EVENTOS ----------
                if (eventosDoDia.isEmpty()) {
                    Text(
                        "Nenhum evento",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                } else {
                    eventosDoDia.forEach { evento ->
                        EventoItem(evento, eventos) { excluido ->
                            eventos = eventos.filter { it.id != excluido.id }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text("Criar novo evento", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(12.dp))

                val radius = 16.dp

                OutlinedTextField(
                    value = novoNome,
                    onValueChange = { novoNome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(radius)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = novaDescricao,
                    onValueChange = { novaDescricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(radius)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = novaHora,
                    onValueChange = { novaHora = it },
                    label = { Text("Hora (HH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(radius)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = novoLink,
                    onValueChange = { novoLink = it },
                    label = { Text("Link") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(radius)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (novoNome.isNotBlank() && novaDescricao.isNotBlank()) {
                            val dataHora = "${dataSelecionada}T${novaHora}:00"
                            val novoEventoReq = NovoEventoRequest(
                                nome_evento = novoNome,
                                data_evento = dataHora,
                                descricao = novaDescricao,
                                link = novoLink,
                                id_grupo = grupoId,
                                id_usuario = idUsuario
                            )

                            RetrofitInstance.calendarioService.criarEvento(novoEventoReq)
                                .enqueue(object : Callback<CalendarioResponseWrapper> {
                                    override fun onResponse(
                                        call: Call<CalendarioResponseWrapper>,
                                        response: Response<CalendarioResponseWrapper>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Evento criado!", Toast.LENGTH_SHORT).show()
                                            eventos = eventos + Evento(
                                                id = (eventos.maxOfOrNull { it.id } ?: 0) + 1,
                                                data = dataSelecionada!!,
                                                nome = novoNome,
                                                descricao = novaDescricao,
                                                hora = novaHora,
                                                link = novoLink,
                                                grupoId = grupoId
                                            )
                                            novoNome = ""
                                            novaDescricao = ""
                                            novoLink = ""
                                            novaHora = ""
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<CalendarioResponseWrapper>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A39C7)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Salvar evento", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}


@Composable
private fun EventoItem(evento: Evento, eventos: List<Evento>, onDelete: (Evento) -> Unit) {
    var confirmar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6E2FF), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(evento.nome, fontWeight = FontWeight.Bold)

            IconButton(onClick = { confirmar = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = Color.Red
                )
            }
        }

        Text("Descrição: ${evento.descricao}")
        evento.hora?.let { Text("Hora: $it") }
        if (evento.link.isNotBlank()) Text("Link: ${evento.link}", color = Color(0xFF341E9B))

        if (confirmar) {
            AlertDialog(
                onDismissRequest = { confirmar = false },
                confirmButton = {
                    TextButton(onClick = {
                        confirmar = false
                        onDelete(evento)
                        RetrofitInstance.calendarioService.excluirEvento(evento.id)
                            .enqueue(object : Callback<CalendarioResponseWrapper> {
                                override fun onResponse(
                                    call: Call<CalendarioResponseWrapper>,
                                    response: Response<CalendarioResponseWrapper>
                                ) {}
                                override fun onFailure(
                                    call: Call<CalendarioResponseWrapper>,
                                    t: Throwable
                                ) {}
                            })
                    }) {
                        Text("Sim")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmar = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Excluir evento") },
                text = { Text("Tem certeza que deseja excluir este evento?") }
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}
