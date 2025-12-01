package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.R
import com.example.app_journey.ui.theme.DarkPrimaryPurple
import com.example.app_journey.ui.theme.LightAccent
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White

data class EbookDetail(
    val coverId: Int,
    val title: String,
    val price: String,
    val categories: List<String>
)

@Composable
fun Chip(category: String, backgroundColor: Color, textColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = category,
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun DetalheEbookScreen(onAdicionarCarrinho: () -> Unit, onVoltar: () -> Unit) {
    val PurpleDark = DarkPrimaryPurple
    val TextLight = White
    val BackgroundScreen = LightAccent
    val TextDark = PrimaryPurple

    val ebook = EbookDetail(
        coverId = R.drawable.logoclaro,
        title = "Liberdade Financeira",
        price = "R$49,99",
        categories = listOf("Finanças", "Autodesenvolvimento")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundScreen)
            .verticalScroll(rememberScrollState())
    ) {

        // TopBar customizada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(PurpleDark)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltar) {
                Icon(
                    painter = painterResource(id = R.drawable.logoclaro),
                    contentDescription = "Voltar",
                    tint = TextLight
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Journey",
                color = TextLight,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Capa do e-book
        Image(
            painter = painterResource(id = ebook.coverId),
            contentDescription = ebook.title,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(0.65f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = ebook.title,
                color = PurpleDark,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Categorias:",
                color = PurpleDark,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ebook.categories.forEach { category ->
                    Chip(category = category, backgroundColor = LightAccent, textColor = TextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de adicionar ao carrinho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ebook.price,
                color = PurpleDark,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 20.sp
            )
            Button(
                onClick = onAdicionarCarrinho,
                colors = ButtonDefaults.buttonColors(containerColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Adicionar ao carrinho",
                    color = PurpleDark,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
        }
    }
}