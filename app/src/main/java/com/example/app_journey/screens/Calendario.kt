package com.example.app_journey.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

// =============== CORES DO TEMA ===============
private val CalPrimaryPurple = Color(0xFF6C5CE7)
private val CalPrimaryDark = Color(0xFF4A3CB5)
private val CalAccent = Color(0xFF8B7CF7)
private val CalSoftPurple = Color(0xFFE8E5FF)
private val CalBackground = Color(0xFFF8F9FE)
private val CalTextDark = Color(0xFF1A1A2E)
private val CalTextGray = Color(0xFF6B7280)
private val CalCardWhite = Color(0xFFFFFFFF)
private val CalSuccess = Color(0xFF10B981)
private val CalWarning = Color(0xFFFFB347)
private val CalError = Color(0xFFEF4444)

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
    var isLoading by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Campos do novo evento
    var novoNome by remember { mutableStateOf("") }
    var novaDescricao by remember { mutableStateOf("") }
    var novoLink by remember { mutableStateOf("") }
    var novaHora by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Busca eventos
    LaunchedEffect(Unit) {
        RetrofitInstance.calendarioService.getTodosEventos()
            .enqueue(object : Callback<CalendarioResponseWrapper> {
                override fun onResponse(
                    call: Call<CalendarioResponseWrapper>,
                    response: Response<CalendarioResponseWrapper>
                ) {
                    isLoading = false
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

                override fun onFailure(call: Call<CalendarioResponseWrapper>, t: Throwable) {
                    isLoading = false
                }
            })
    }

    // Estrutura do calendário
    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()

    val diasCalendario = buildList<LocalDate?> {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    // Contagem de eventos no mês
    val eventosNoMes = eventos.filter {
        it.data.month == mesAtual.month && it.data.year == mesAtual.year
    }.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalBackground)
    ) {
        // Header com gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CalPrimaryPurple, CalPrimaryDark)
                    )
                )
        )

        // Elementos decorativos
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-30).dp, y = (-20).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 320.dp, y = 40.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // =============== TOP BAR ===============
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Calendário",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "$eventosNoMes eventos este mês",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Botão ir para hoje
                IconButton(
                    onClick = { mesAtual = YearMonth.now() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Hoje",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // =============== CARD DO CALENDÁRIO ===============
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = CalCardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Handle decorativo
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== SELETOR DE MÊS ===============
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CalSoftPurple, RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { mesAtual = mesAtual.minusMonths(1) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(CalCardWhite, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Mês anterior",
                                tint = CalPrimaryPurple
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = mesAtual.month.getDisplayName(
                                    TextStyle.FULL,
                                    Locale("pt", "BR")
                                ).replaceFirstChar { it.uppercase() },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalTextDark
                            )
                            Text(
                                text = mesAtual.year.toString(),
                                fontSize = 14.sp,
                                color = CalTextGray
                            )
                        }

                        IconButton(
                            onClick = { mesAtual = mesAtual.plusMonths(1) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(CalCardWhite, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "Próximo mês",
                                tint = CalPrimaryPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== DIAS DA SEMANA ===============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb").forEach { dia ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    dia,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalTextGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // =============== GRADE DO CALENDÁRIO ===============
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CalPrimaryPurple)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(diasCalendario.size) { index ->
                                val dia = diasCalendario[index]

                                if (dia == null) {
                                    Box(modifier = Modifier.aspectRatio(1f))
                                } else {
                                    val eventosDoDia = eventos.filter { it.data == dia }
                                    val isHoje = dia == hoje
                                    val isPassado = dia.isBefore(hoje)
                                    val temEventos = eventosDoDia.isNotEmpty()

                                    DiaCalendario(
                                        dia = dia,
                                        isHoje = isHoje,
                                        isPassado = isPassado,
                                        temEventos = temEventos,
                                        quantidadeEventos = eventosDoDia.size,
                                        onClick = {
                                            dataSelecionada = dia
                                            coroutineScope.launch { sheetState.show() }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // =============== LEGENDA ===============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendaItem(color = CalPrimaryPurple, text = "Hoje")
                        Spacer(modifier = Modifier.width(20.dp))
                        LegendaItem(color = CalSuccess, text = "Com eventos")
                        Spacer(modifier = Modifier.width(20.dp))
                        LegendaItem(color = CalTextGray.copy(alpha = 0.3f), text = "Passado")
                    }
                }
            }
        }
    }

    // =============== BOTTOM SHEET ===============
    if (dataSelecionada != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { dataSelecionada = null },
            containerColor = CalCardWhite,
            tonalElevation = 0.dp,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
        ) {
            EventosBottomSheet(
                dataSelecionada = dataSelecionada!!,
                eventos = eventos.filter { it.data == dataSelecionada },
                novoNome = novoNome,
                onNomeChange = { novoNome = it },
                novaDescricao = novaDescricao,
                onDescricaoChange = { novaDescricao = it },
                novaHora = novaHora,
                onHoraChange = { novaHora = it },
                novoLink = novoLink,
                onLinkChange = { novoLink = it },
                onSalvar = {
                    if (novoNome.isNotBlank() && novaDescricao.isNotBlank()) {
                        val dataHora = "${dataSelecionada}T${novaHora.ifBlank { "12:00" }}:00"
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
                                            hora = novaHora.ifBlank { "12:00" },
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
                                    Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                },
                onDeleteEvento = { evento ->
                    eventos = eventos.filter { it.id != evento.id }
                    RetrofitInstance.calendarioService.excluirEvento(evento.id)
                        .enqueue(object : Callback<CalendarioResponseWrapper> {
                            override fun onResponse(
                                call: Call<CalendarioResponseWrapper>,
                                response: Response<CalendarioResponseWrapper>
                            ) {
                                Toast.makeText(context, "Evento excluído!", Toast.LENGTH_SHORT).show()
                            }
                            override fun onFailure(
                                call: Call<CalendarioResponseWrapper>,
                                t: Throwable
                            ) {}
                        })
                }
            )
        }
    }
}

// =============== DIA DO CALENDÁRIO ===============
@Composable
private fun DiaCalendario(
    dia: LocalDate,
    isHoje: Boolean,
    isPassado: Boolean,
    temEventos: Boolean,
    quantidadeEventos: Int,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isHoje -> CalPrimaryPurple
        temEventos -> CalSuccess.copy(alpha = 0.15f)
        isPassado -> Color.Gray.copy(alpha = 0.05f)
        else -> CalSoftPurple.copy(alpha = 0.3f)
    }

    val textColor = when {
        isHoje -> Color.White
        isPassado -> CalTextGray.copy(alpha = 0.5f)
        else -> CalTextDark
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dia.dayOfMonth.toString(),
                fontSize = 14.sp,
                fontWeight = if (isHoje) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )

            if (temEventos && !isHoje) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(minOf(quantidadeEventos, 3)) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .padding(horizontal = 1.dp)
                                .background(CalSuccess, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

// =============== LEGENDA ===============
@Composable
private fun LegendaItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = CalTextGray
        )
    }
}

// =============== BOTTOM SHEET CONTENT ===============
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventosBottomSheet(
    dataSelecionada: LocalDate,
    eventos: List<Evento>,
    novoNome: String,
    onNomeChange: (String) -> Unit,
    novaDescricao: String,
    onDescricaoChange: (String) -> Unit,
    novaHora: String,
    onHoraChange: (String) -> Unit,
    novoLink: String,
    onLinkChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onDeleteEvento: (Evento) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header da data
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(listOf(CalPrimaryPurple, CalAccent)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dataSelecionada.dayOfMonth.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = dataSelecionada.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale("pt", "BR")
                    ).replaceFirstChar { it.uppercase() },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTextDark
                )
                Text(
                    text = "${dataSelecionada.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} de ${dataSelecionada.year}",
                    fontSize = 14.sp,
                    color = CalTextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de eventos
        if (eventos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CalSoftPurple.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📅", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Nenhum evento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = CalTextDark
                    )
                    Text(
                        "Adicione um novo evento abaixo",
                        fontSize = 14.sp,
                        color = CalTextGray
                    )
                }
            }
        } else {
            Text(
                "Eventos (${eventos.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CalTextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            eventos.forEach { evento ->
                EventoCard(
                    evento = evento,
                    onDelete = { onDeleteEvento(evento) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Toggle formulário
        AnimatedVisibility(
            visible = !showForm,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = { showForm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CalPrimaryPurple)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Novo Evento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Formulário
        AnimatedVisibility(
            visible = showForm,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CalSoftPurple.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Novo Evento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalTextDark
                        )
                        IconButton(
                            onClick = { showForm = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = CalTextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos
                    FormField(
                        value = novoNome,
                        onValueChange = onNomeChange,
                        label = "Nome do evento",
                        icon = Icons.Default.Edit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormField(
                        value = novaDescricao,
                        onValueChange = onDescricaoChange,
                        label = "Descrição",
                        icon = Icons.Default.Info
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            FormField(
                                value = novaHora,
                                onValueChange = onHoraChange,
                                label = "Hora (HH:mm)",
                                icon = Icons.Default.DateRange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FormField(
                        value = novoLink,
                        onValueChange = onLinkChange,
                        label = "Link (opcional)",
                        icon = Icons.Default.Share
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onSalvar()
                            showForm = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CalSuccess),
                        enabled = novoNome.isNotBlank() && novaDescricao.isNotBlank()
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Salvar Evento",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// =============== FORM FIELD ===============
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = CalPrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CalPrimaryPurple,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = CalCardWhite,
            unfocusedContainerColor = CalCardWhite
        ),
        singleLine = true
    )
}

// =============== EVENTO CARD ===============
@Composable
private fun EventoCard(
    evento: Evento,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CalCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Indicador de cor
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(CalPrimaryPurple, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = evento.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = evento.descricao,
                    fontSize = 14.sp,
                    color = CalTextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!evento.hora.isNullOrBlank()) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CalAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evento.hora,
                            fontSize = 12.sp,
                            color = CalAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (evento.link.isNotBlank()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = CalSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Link",
                            fontSize = 12.sp,
                            color = CalSuccess,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = CalError.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Dialog de confirmação
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = CalCardWhite,
            title = {
                Text(
                    "Excluir evento",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Tem certeza que deseja excluir \"${evento.nome}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CalError),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = CalTextGray)
                }
            }
        )
    }
}