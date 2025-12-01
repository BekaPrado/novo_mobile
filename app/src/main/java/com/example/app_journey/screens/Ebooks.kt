package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.model.Ebook
import com.example.app_journey.service.EbookService
import com.example.app_journey.ui.theme.LightAccent
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEbooksScreen(
    ebookService: EbookService,
    onEbookClick: (Int) -> Unit,
    onCriarClick: () -> Unit,
    onCarrinhoClick: () -> Unit
) {
    var ebooks by remember { mutableStateOf(listOf<Ebook>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    // Chamada do backend
    LaunchedEffect(Unit) {
        try {
            val response = ebookService.getTodosEbooks()
            ebooks = response.ebooks ?: emptyList()
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey E-books", color = White) },
                navigationIcon = {},
                actions = {
                    IconButton(onClick = onCriarClick) {
                        Icon(Icons.Default.Add, contentDescription = "Criar", tint = White)
                    }
                    IconButton(onClick = onCarrinhoClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple)
            )
        },
        containerColor = LightAccent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo de busca estilizado
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Pesquisar e-book", color = PrimaryPurple.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple.copy(alpha = 0.3f),
                    unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.2f),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = PrimaryPurple,
                    focusedTextColor = PrimaryPurple,
                    unfocusedTextColor = PrimaryPurple
                )
            )

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Text("Carregando e-books...", color = PrimaryPurple)
            } else if (errorMessage != null) {
                Text(errorMessage ?: "Erro desconhecido", color = Color.Red)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ebooks.filter { it.titulo.contains(searchText, ignoreCase = true) }) { ebook ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEbookClick(ebook.id_ebooks) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(PrimaryPurple, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Espaço para capa do ebook
                                    // Se tiver imagem, usar Image(...)
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    ebook.titulo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryPurple,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    "R$ ${ebook.preco}",
                                    color = PrimaryPurple,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
