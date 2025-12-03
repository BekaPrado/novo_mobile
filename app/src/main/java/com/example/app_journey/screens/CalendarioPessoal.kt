package com.example.app_journey.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioPessoal(
    navController: NavHostController,
    idUsuario: Int
) {
    val hoje = remember { LocalDate.now() }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(listOf<Evento>()) }
    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Carregar eventos
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
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CalendarioResponseWrapper>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
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

    // Contagem de eventos
    val eventosNoMes = eventos.filter {
        it.data.month == mesAtual.month && it.data.year == mesAtual.year
    }.size

    val eventosHoje = eventos.filter { it.data == hoje }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalBackground)
    ) {
        // Header com gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CalPrimaryPurple, CalPrimaryDark)
                    )
                )
        )

        // Elementos decorativos
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-50).dp, y = (-30).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 300.dp, y = 50.dp)
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
                        "Meu Calendário",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Organize seus compromissos",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // =============== CARD RESUMO DO DIA ===============
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CalCardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Data de hoje
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.linearGradient(listOf(CalPrimaryPurple, CalAccent)),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hoje.dayOfMonth.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = hoje.dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale("pt", "BR")
                                ).uppercase(),
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hoje",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalTextDark
                        )
                        Text(
                            text = if (eventosHoje.isEmpty())
                                "Nenhum evento agendado"
                            else
                                "${eventosHoje.size} evento(s) hoje",
                            fontSize = 14.sp,
                            color = if (eventosHoje.isEmpty()) CalTextGray else CalSuccess
                        )
                    }

                    // Badge de eventos do mês
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CalSoftPurple
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = eventosNoMes.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalPrimaryPurple
                            )
                            Text(
                                text = "este mês",
                                fontSize = 10.sp,
                                color = CalTextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

                    Spacer(modifier = Modifier.height(16.dp))

                    // =============== SELETOR DE MÊS ===============
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CalSoftPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalTextDark
                            )
                            Text(
                                text = mesAtual.year.toString(),
                                fontSize = 13.sp,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // =============== DIAS DA SEMANA ===============
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
                                    fontSize = 13.sp,
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

                                    DiaPessoalCalendario(
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // =============== LEGENDA ===============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendaItemPessoal(color = CalPrimaryPurple, text = "Hoje")
                        Spacer(modifier = Modifier.width(16.dp))
                        LegendaItemPessoal(color = CalSuccess, text = "Com eventos")
                        Spacer(modifier = Modifier.width(16.dp))
                        LegendaItemPessoal(color = CalWarning, text = "Próximo")
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
            EventosPessoaisBottomSheet(
                dataSelecionada = dataSelecionada!!,
                eventos = eventos.filter { it.data == dataSelecionada }
            )
        }
    }
}

// =============== DIA DO CALENDÁRIO PESSOAL ===============
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DiaPessoalCalendario(
    dia: LocalDate,
    isHoje: Boolean,
    isPassado: Boolean,
    temEventos: Boolean,
    quantidadeEventos: Int,
    onClick: () -> Unit
) {
    val amanha = LocalDate.now().plusDays(1)
    val isProximo = dia == amanha && temEventos

    val backgroundColor = when {
        isHoje -> CalPrimaryPurple
        isProximo -> CalWarning.copy(alpha = 0.2f)
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
            .then(
                if (isHoje) Modifier.border(
                    2.dp,
                    Color.White.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
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
                fontWeight = if (isHoje || temEventos) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )

            if (temEventos && !isHoje) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(minOf(quantidadeEventos, 3)) { i ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .size(5.dp)
                                .background(
                                    if (isProximo) CalWarning else CalSuccess,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

// =============== LEGENDA ===============
@Composable
private fun LegendaItemPessoal(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = CalTextGray
        )
    }
}

// =============== BOTTOM SHEET CONTENT ===============
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventosPessoaisBottomSheet(
    dataSelecionada: LocalDate,
    eventos: List<Evento>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
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
                    .size(60.dp)
                    .background(
                        Brush.linearGradient(listOf(CalPrimaryPurple, CalAccent)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dataSelecionada.dayOfMonth.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = dataSelecionada.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale("pt", "BR")
                        ).uppercase(),
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
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
                        "Aproveite o dia livre!",
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
                EventoPessoalCard(evento = evento)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// =============== EVENTO CARD ===============
@Composable
private fun EventoPessoalCard(evento: Evento) {
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
                    .height(50.dp)
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

                if (evento.descricao.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = evento.descricao,
                        fontSize = 14.sp,
                        color = CalTextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
        }
    }
}