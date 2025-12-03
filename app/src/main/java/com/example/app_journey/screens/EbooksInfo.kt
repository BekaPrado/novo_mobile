package com.example.app_journey.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.model.Ebook
import com.example.app_journey.service.EbookService

// =============== PALETA DE CORES ===============
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF5849C2)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val BackgroundWhite = Color(0xFFFAFAFF)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF6B7280)
private val TextMuted = Color(0xFF9CA3AF)
private val AccentGreen = Color(0xFF10B981)
private val AccentOrange = Color(0xFFFF9F43)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentPink = Color(0xFFEC4899)
private val AccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun EbooksFloatingElements(bounce: Float, glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 300.dp, y = (40 + bounce * 0.5f).dp)
            .size(80.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = (-30).dp, y = (100 + bounce).dp)
            .size(60.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape)
    )

    Box(
        modifier = Modifier
            .offset(x = 340.dp, y = (150 + bounce * 0.7f).dp)
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
    )

    listOf(
        Triple(70.dp, 50.dp, 5.dp),
        Triple(280.dp, 90.dp, 4.dp),
        Triple(160.dp, 130.dp, 3.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.6f), CircleShape)
        )
    }
}

// =============== CARD DE EBOOK MODERNO ===============
@Composable
private fun ModernEbookCard(
    ebook: Ebook,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val colors = listOf(AccentPurple, AccentBlue, AccentPink, AccentGreen, AccentOrange)
    val coverColor = colors[index % colors.size]

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300, delayMillis = index * 80)) +
                scaleIn(tween(300, delayMillis = index * 80), initialScale = 0.8f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Capa do ebook
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(coverColor, coverColor.copy(alpha = 0.7f))
                            ),
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📘", fontSize = 48.sp)

                    if (index % 2 == 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(AccentOrange, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-20%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = ebook.titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R$ ${ebook.preco}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "4.${5 + (index % 5)}",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }
        }
    }
}