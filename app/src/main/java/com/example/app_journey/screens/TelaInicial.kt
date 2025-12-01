package com.example.app_journey.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R

@Composable
fun TelaInicial(navController: NavHostController) {
    var mostrarTutorial by remember { mutableStateOf(true) }
    var buttonPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    var buttonSize by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF341E9B)) // Fundo roxo
    ) {
        // Fundo ilustrativo
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Background Journey",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.55f)
                .offset(y = (-80).dp)
        )

        // Conteúdo principal
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Comece sua\naventura com\nJourney!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Botão de avançar
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .onGloballyPositioned { coordinates ->
                        val pos = coordinates.localToWindow(Offset.Zero)
                        buttonPosition = IntOffset(pos.x.toInt(), pos.y.toInt())
                        buttonSize = coordinates.size.width.dp
                    }
                    .clip(CircleShape)
                    .background(Color.Transparent.copy(alpha = 0.2f))
                    .clickable { navController.navigate("login") },
                contentAlignment = Alignment.Center
            ) {
                val scale = remember { mutableStateOf(true) }
                val animatedScale by animateFloatAsState(
                    targetValue = if (scale.value) 1.45f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                LaunchedEffect(Unit) { scale.value = !scale.value }

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Avançar",
                        tint = Color.White,
                        modifier = Modifier.scale(animatedScale)
                    )
                }
            }
        }

//        // Overlay de tutorial
//        if (mostrarTutorial) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .clickable { mostrarTutorial = false } // fecha ao tocar
//            ) {
//                // Texto explicativo e seta
//                Column(
//                    modifier = Modifier
//                        .offset {
//                            IntOffset(
//                                buttonPosition.x - 320,
//                                buttonPosition.y - 400 // ajusta para o texto acima do botão
//                            )
//                        }
//                        .width(200.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Clique neste botão para entrar no app!",
//                        color = Color.White,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Center
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    val infiniteTransition = rememberInfiniteTransition()
//                    val arrowOffset by infiniteTransition.animateFloat(
//                        initialValue = 0f,
//                        targetValue = 10f,
//                        animationSpec = infiniteRepeatable(
//                            animation = tween(500, easing = LinearEasing),
//                            repeatMode = RepeatMode.Reverse
//                        )
//                    )
//
//                    Icon(
//                        imageVector = Icons.Default.ArrowForward,
//                        contentDescription = "Seta tutorial",
//                        tint = Color.Yellow,
//                        modifier = Modifier
//                            .size(36.dp)
//                            .rotate(45f)
//                            .offset(y = arrowOffset.dp)
//                    )
//                }
//            }
//        }
    }
}
