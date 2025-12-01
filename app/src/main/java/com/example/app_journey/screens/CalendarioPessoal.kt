package com.example.app_journey.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
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

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Carregar eventos
    LaunchedEffect(Unit) {
        RetrofitInstance.calendarioService.getTodosEventos().enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
            override fun onResponse(call: retrofit2.Call<CalendarioResponseWrapper>, response: retrofit2.Response<CalendarioResponseWrapper>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true && body.Calendario != null) {
                        eventos = body.Calendario.mapNotNull { item ->
                            try {
                                val data = LocalDate.parse(item.data_evento.substring(0, 10))
                                val hora = item.data_evento.substring(11, 16)
                                Evento(item.id_calendario, data, item.nome_evento, item.descricao, hora, item.link, item.id_grupo)
                            } catch (e: Exception) { null }
                        }
                    }
                } else {
                    Toast.makeText(context, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🔹 Dias do calendário
    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()
    val diasCalendario = buildList<LocalDate?> {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    // 🔹 UI
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5FF))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Cabeçalho do mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                    Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${mesAtual.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() }} ${mesAtual.year}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                    Text(">", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dias da semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("D","S","T","Q","Q","S","S").forEach { dia ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(dia, fontWeight = FontWeight.Bold, color = Color(0xFF341E9B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grade de dias
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(diasCalendario.size) { index ->
                    val dia = diasCalendario[index]
                    if (dia == null) Box(modifier = Modifier.aspectRatio(1f)) {}
                    else {
                        val eventosDoDia = eventos.filter { it.data == dia }
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    dataSelecionada = dia
                                    coroutineScope.launch { sheetState.show() }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    dia == hoje -> Color(0xFF341E9B)
                                    eventosDoDia.isNotEmpty() -> Color(0xFFDAD5FF)
                                    else -> Color(0xFFEDEEFF)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = dia.dayOfMonth.toString(),
                                    fontWeight = if (dia == hoje) FontWeight.Bold else FontWeight.Medium,
                                    color = if (dia == hoje) Color.White else Color.Black
                                )
                                eventosDoDia.take(2).forEach { evento ->
                                    Text(evento.nome, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
                                if (eventosDoDia.size > 2)
                                    Text("+${eventosDoDia.size - 2} mais", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // 🔹 BottomSheet
        if (dataSelecionada != null) {
            ModalBottomSheet(
                onDismissRequest = { dataSelecionada = null },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(
                        text = "Eventos de ${dataSelecionada!!.dayOfMonth}/${dataSelecionada!!.monthValue}/${dataSelecionada!!.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val eventosDoDia = eventos.filter { it.data == dataSelecionada }
                    if (eventosDoDia.isEmpty()) {
                        Text("Nenhum evento", color = Color.Gray)
                    } else {
                        eventosDoDia.forEach { evento ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0FF))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(evento.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF341E9B))
                                    if (evento.descricao.isNotBlank()) Text(evento.descricao, fontSize = 14.sp, color = Color.Black)
                                    if (!evento.hora.isNullOrBlank()) Text("Hora: ${evento.hora}", fontSize = 12.sp, color = Color.Gray)
                                    if (evento.link.isNotBlank()) Text("Link: ${evento.link}", fontSize = 12.sp, color = Color(0xFF341E9B))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}