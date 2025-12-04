package com.example.app_journey.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// =============== PALETA VIBRANTE MODERNA ===============
private val VibrantPurple = Color(0xFF5B5BF7)
private val ElectricBlue = Color(0xFF6C63FF)
private val BrightViolet = Color(0xFF7C73FF)
private val NeonPurple = Color(0xFF8B83FF)
private val SoftLavender = Color(0xFFB8B5FF)
private val PaleLavender = Color(0xFFE8E7FF)
private val DeepIndigo = Color(0xFF3D3A9E)
private val DarkSurface = Color(0xFF1A1840)
private val DarkBackground = Color(0xFF0E0C24)
private val TextWhite = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFFA5A3C7)
private val GlassWhite = Color(0xFFFFFFFF)

@Composable
fun TelaInicial(navController: NavHostController) {

    val infiniteTransition = rememberInfiniteTransition(label = "main")

    // Animação dos blobs
    val blobAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob"
    )

    // Pulsação
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Glow intensity
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Rotação do anel
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )

    // Animações de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 200),
        label = "alpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val textOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 40.dp,
        animationSpec = tween(1000, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "textOffset"
    )

    val buttonOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 60.dp,
        animationSpec = tween(1000, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "buttonOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface,
                        DarkBackground
                    )
                )
            )
    ) {
        // =============== LAYER 1: ANIMATED BLOBS ===============
        AnimatedBlobs(blobAnim, glowPulse)

        // =============== LAYER 2: GRID PATTERN ===============
        GridPattern()

        // =============== LAYER 3: FLOATING PARTICLES ===============
        FloatingParticles()

        // =============== LAYER 4: STARS ===============
        AnimatedStars()

        // =============== MAIN CONTENT ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.12f))

            // =============== LOGO SECTION ===============
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.42f)
                    .graphicsLayer {
                        alpha = contentAlpha
                        scaleX = logoScale
                        scaleY = logoScale
                    }
            ) {
                // Glow externo grande
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .scale(pulse)
                        .blur(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    VibrantPurple.copy(alpha = glowPulse * 0.5f),
                                    ElectricBlue.copy(alpha = glowPulse * 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Anel rotativo externo
                Canvas(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer { rotationZ = ringRotation }
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2

                    // Pontos no anel
                    for (i in 0..11) {
                        val angle = Math.toRadians((i * 30).toDouble())
                        val x = center.x + cos(angle).toFloat() * radius
                        val y = center.y + sin(angle).toFloat() * radius

                        drawCircle(
                            color = if (i % 3 == 0) NeonPurple else BrightViolet.copy(alpha = 0.5f),
                            radius = if (i % 3 == 0) 6f else 3f,
                            center = Offset(x, y)
                        )
                    }
                }

                // Anel gradiente
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .scale(pulse)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    VibrantPurple,
                                    ElectricBlue,
                                    BrightViolet,
                                    NeonPurple,
                                    VibrantPurple
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Container glass da logo
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GlassWhite.copy(alpha = 0.15f),
                                    GlassWhite.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GlassWhite.copy(alpha = 0.4f),
                                    GlassWhite.copy(alpha = 0.1f),
                                    GlassWhite.copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner glow
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .blur(30.dp)
                            .background(
                                VibrantPurple.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.brancologo),
                        contentDescription = "Journey Logo",
                        modifier = Modifier.size(110.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Partículas orbitando
                OrbitingParticles(ringRotation)
            }

            // =============== TEXT SECTION ===============
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .graphicsLayer {
                        alpha = contentAlpha
                        translationY = textOffset.value
                    },
                horizontalAlignment = Alignment.Start
            ) {
                // Badge


                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Evolua com",
                    fontSize = 34.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.5).sp
                )

                // Nome com gradiente vibrante
                Text(
                    text = "Journey",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp,
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                TextWhite,
                                NeonPurple,
                                VibrantPurple,
                                ElectricBlue
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Transforme seu aprendizado em uma\nexperiência única e envolvente.",
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Normal
                )
            }

            // =============== BUTTON SECTION ===============
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.16f)
                    .graphicsLayer {
                        alpha = contentAlpha
                        translationY = buttonOffset.value
                    },
                contentAlignment = Alignment.Center
            ) {
                ModernGlowButton(
                    onClick = { navController.navigate("login") },
                    glowIntensity = glowPulse
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =============== PAGE INDICATORS ===============
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .graphicsLayer { alpha = contentAlpha },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == 0) 32.dp else 10.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (index == 0)
                                    Brush.horizontalGradient(
                                        listOf(VibrantPurple, ElectricBlue)
                                    )
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            TextMuted.copy(alpha = 0.3f),
                                            TextMuted.copy(alpha = 0.3f)
                                        )
                                    )
                            )
                    )
                }
            }
        }
    }
}

// =============== ANIMATED BLOBS ===============
@Composable
private fun AnimatedBlobs(animOffset: Float, intensity: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Blob 1 - Superior direito
        val blob1X = size.width * 0.85f + cos(Math.toRadians(animOffset.toDouble())).toFloat() * 40f
        val blob1Y = size.height * 0.1f + sin(Math.toRadians(animOffset.toDouble())).toFloat() * 30f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    VibrantPurple.copy(alpha = intensity * 0.4f),
                    ElectricBlue.copy(alpha = intensity * 0.2f),
                    Color.Transparent
                ),
                center = Offset(blob1X, blob1Y),
                radius = 350f
            ),
            center = Offset(blob1X, blob1Y),
            radius = 350f
        )

        // Blob 2 - Inferior esquerdo
        val blob2X = size.width * 0.15f + sin(Math.toRadians(animOffset * 0.8).toDouble()).toFloat() * 35f
        val blob2Y = size.height * 0.75f + cos(Math.toRadians(animOffset * 0.8).toDouble()).toFloat() * 35f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrightViolet.copy(alpha = intensity * 0.35f),
                    NeonPurple.copy(alpha = intensity * 0.15f),
                    Color.Transparent
                ),
                center = Offset(blob2X, blob2Y),
                radius = 400f
            ),
            center = Offset(blob2X, blob2Y),
            radius = 400f
        )

        // Blob 3 - Central
        val blob3X = size.width * 0.5f + cos(Math.toRadians(animOffset * 1.2).toDouble()).toFloat() * 20f
        val blob3Y = size.height * 0.35f + sin(Math.toRadians(animOffset * 1.2).toDouble()).toFloat() * 20f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = intensity * 0.3f),
                    VibrantPurple.copy(alpha = intensity * 0.1f),
                    Color.Transparent
                ),
                center = Offset(blob3X, blob3Y),
                radius = 450f
            ),
            center = Offset(blob3X, blob3Y),
            radius = 450f
        )
    }
}

// =============== GRID PATTERN ===============
@Composable
private fun GridPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val lineColor = GlassWhite.copy(alpha = 0.025f)

        var x = 0f
        while (x < size.width) {
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += gridSize
        }

        var y = 0f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += gridSize
        }
    }
}

// =============== FLOATING PARTICLES ===============
@Composable
private fun FloatingParticles() {
    val particles = remember {
        List(20) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.4f + 0.15f,
                color = listOf(VibrantPurple, ElectricBlue, BrightViolet, NeonPurple).random()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    particles.forEachIndexed { index, particle ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -120f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (12000 / particle.speed).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particleY_$index"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (6000 / particle.speed).toInt(),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleAlpha_$index"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = (particle.y * size.height + offsetY) % size.height
            val adjustedY = if (y < 0) size.height + y else y

            // Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        particle.color.copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(particle.x * size.width, adjustedY),
                    radius = particle.size * 4
                ),
                center = Offset(particle.x * size.width, adjustedY),
                radius = particle.size * 4
            )

            // Core
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.size,
                center = Offset(particle.x * size.width, adjustedY)
            )
        }
    }
}

private data class ParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

// =============== ANIMATED STARS ===============
@Composable
private fun AnimatedStars() {
    val stars = remember {
        List(50) {
            StarData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 0.5f,
                twinkleSpeed = Random.nextInt(2000, 4000)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    stars.forEachIndexed { index, star ->
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(star.twinkleSpeed, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "starAlpha_$index"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = SoftLavender.copy(alpha = alpha),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

private data class StarData(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleSpeed: Int
)

// =============== ORBITING PARTICLES ===============
@Composable
private fun OrbitingParticles(rotation: Float) {
    Canvas(modifier = Modifier.size(260.dp)) {
        val center = Offset(size.width / 2, size.height / 2)

        // Órbita 1
        for (i in 0..2) {
            val angle = Math.toRadians((rotation * 1.5 + i * 120).toDouble())
            val x = center.x + cos(angle).toFloat() * 115f
            val y = center.y + sin(angle).toFloat() * 115f

            // Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VibrantPurple.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = 15f
                ),
                center = Offset(x, y),
                radius = 15f
            )

            drawCircle(
                color = PaleLavender,
                radius = 4f,
                center = Offset(x, y)
            )
        }

        // Órbita 2 (mais lenta, sentido oposto)
        for (i in 0..3) {
            val angle = Math.toRadians((-rotation * 0.8 + i * 90).toDouble())
            val x = center.x + cos(angle).toFloat() * 135f
            val y = center.y + sin(angle).toFloat() * 135f

            drawCircle(
                color = NeonPurple.copy(alpha = 0.7f),
                radius = 3f,
                center = Offset(x, y)
            )
        }
    }
}

// =============== MODERN GLOW BUTTON ===============
@Composable
private fun ModernGlowButton(
    onClick: () -> Unit,
    glowIntensity: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "button")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(contentAlignment = Alignment.Center) {
        // Glow atrás do botão
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .blur(25.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            VibrantPurple.copy(alpha = glowIntensity * 0.5f),
                            ElectricBlue.copy(alpha = glowIntensity * 0.4f),
                            VibrantPurple.copy(alpha = glowIntensity * 0.5f)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
        )

        // Botão principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            VibrantPurple,
                            ElectricBlue,
                            VibrantPurple
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            GlassWhite.copy(alpha = 0.3f),
                            GlassWhite.copy(alpha = 0.1f),
                            GlassWhite.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .drawWithContent {
                    drawContent()
                    // Shimmer
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GlassWhite.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            startX = size.width * (shimmer - 0.3f),
                            endX = size.width * (shimmer + 0.3f)
                        )
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Começar agora",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GlassWhite.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}