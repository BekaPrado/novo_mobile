package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.DarkPrimaryPurple
import com.example.app_journey.ui.theme.LightAccent
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White

data class CartEbook(
    val id: Int,
    val titulo: String,
    val preco: String,
    var quantidade: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(onFinalizar: () -> Unit, onVoltar: () -> Unit) {
    val darkPurple = DarkPrimaryPurple
    val lightPurple = LightAccent
    val backgroundWhite = White

    // Lista simulada de e-books no carrinho
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartEbook(1, "Rise Above", "R$50,00"),
                CartEbook(2, "Segredos do Sucesso", "R$60,00"),
                CartEbook(3, "Liberdade Financeira", "R$45,00")
            )
        )
    }

    val handleQuantityChange: (CartEbook, Int) -> Unit = { item, qty ->
        cartItems = cartItems.map { if (it.id == item.id) it.copy(quantidade = qty) else it }
    }

    val total = cartItems.sumOf {
        it.preco.replace("R$", "").replace(",", ".").toDouble() * it.quantidade
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Carrinho", color = White) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkPurple)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total do Pedido", color = White, fontSize = 12.sp)
                    Text("${cartItems.size} e-books", color = White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
                Text("R$${"%.2f".format(total)}", color = White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                Button(
                    onClick = onFinalizar,
                    colors = ButtonDefaults.buttonColors(containerColor = lightPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Finalizar Compra", color = darkPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = lightPurple
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundWhite)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(PrimaryPurple, shape = RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.titulo, fontSize = 16.sp, color = darkPurple)
                                Spacer(Modifier.height(4.dp))
                                Text(item.preco, fontSize = 14.sp, color = PrimaryPurple)
                            }

                            // Controle de quantidade
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, darkPurple, RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    "-",
                                    modifier = Modifier
                                        .clickable { if (item.quantidade > 1) handleQuantityChange(item, item.quantidade - 1) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    color = darkPurple
                                )
                                Text(
                                    item.quantidade.toString(),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    color = darkPurple
                                )
                                Text(
                                    "+",
                                    modifier = Modifier
                                        .clickable { handleQuantityChange(item, item.quantidade + 1) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    color = darkPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
