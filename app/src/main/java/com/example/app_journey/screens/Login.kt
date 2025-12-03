@file:Suppress("UNUSED_VARIABLE")

package com.example.app_journey.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R
import com.example.app_journey.model.LoginRequest
import com.example.app_journey.model.LoginResponse
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

// =============== PALETA DE CORES ===============
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleLight = Color(0xFF8577F5)
private val AccentPurple = Color(0xFF9D8DF7)
private val SoftPurple = Color(0xFFB4A7F8)
private val CardWhite = Color(0xFFFFFFFF)
private val BackgroundPurple = Color(0xFF7C6CF0)
private val TextDark = Color(0xFF2D3748)
private val TextGray = Color(0xFF718096)
private val TextMuted = Color(0xFFA0AEC0)
private val DividerColor = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFE53E3E)

@Composable
fun Login(navegacao: NavHostController?) {

    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val context = LocalContext.current
    val erro = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var senhaVisivel by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "login")

    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "float"
    )

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardWhite)
    ) {
        // =============== HEADER COLORIDO ===============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PrimaryPurple,
                            BackgroundPurple,
                            PrimaryPurpleLight
                        )
                    )
                )
        ) {
            // Elementos decorativos
            FloatingElements(floatAnim, bounce)

            // =============== LOGO CENTRALIZADA ===============
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow atrás da logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(logoPulse)
                        .blur(40.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Container da logo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(logoPulse)
                        .shadow(20.dp, CircleShape)
                        .background(CardWhite, CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    SoftPurple.copy(alpha = 0.5f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoroxa),
                        contentDescription = "Journey Logo",
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // =============== CARD PRINCIPAL ===============
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(240.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Título
                    Text(
                        text = "Entrar",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bem-vindo de volta ao Journey!",
                        fontSize = 15.sp,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // =============== CAMPO EMAIL ===============
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            placeholder = {
                                Text("Digite seu email", color = TextMuted)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = DividerColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                cursorColor = PrimaryPurple
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // =============== CAMPO SENHA ===============
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Senha",
                            fontSize = 14.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = senha.value,
                            onValueChange = { senha.value = it },
                            placeholder = {
                                Text("Digite sua senha", color = TextMuted)
                            },
                            singleLine = true,
                            visualTransformation = if (senhaVisivel)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                Text(
                                    text = if (senhaVisivel) "Ocultar" else "Mostrar",
                                    color = PrimaryPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable { senhaVisivel = !senhaVisivel }
                                        .padding(end = 4.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = DividerColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                cursorColor = PrimaryPurple
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // =============== BOTÃO LOGIN ===============
                    Button(
                        onClick = {
                            if (email.value.isBlank() || senha.value.isBlank()) {
                                erro.value = "Preencha todos os campos"
                                return@Button
                            }

                            isLoading = true
                            erro.value = null

                            val usuarioService = RetrofitFactory().getUsuarioService()
                            val loginRequest = LoginRequest(email.value, senha.value)

                            usuarioService.loginUsuario(loginRequest)
                                .enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(
                                        call: Call<LoginResponse>,
                                        response: Response<LoginResponse>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful) {
                                            val loginResponse = response.body()
                                            if (loginResponse != null && loginResponse.status) {
                                                loginResponse.usuario?.let { usuario ->
                                                    SharedPrefHelper.salvarUsuario(context, usuario)
                                                    SharedPrefHelper.salvarIdUsuario(context, usuario.id)
                                                    SharedPrefHelper.salvarEmail(context, usuario.email)
                                                    Log.d("Login", "ID: ${usuario.id}")
                                                }
                                                navegacao?.navigate(
                                                    "home/${SharedPrefHelper.recuperarIdUsuario(context)}"
                                                ) {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                erro.value = loginResponse?.message
                                                    ?: "Email ou senha incorretos"
                                            }
                                        } else {
                                            erro.value = "Erro ao fazer login"
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        isLoading = false
                                        erro.value = "Erro de conexão"
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Entrar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Esqueceu a senha
                    Text(
                        text = "Esqueceu sua senha?",
                        color = PrimaryPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            navegacao?.navigate("recuperacao_senha")
                        }
                    )

                    // Mensagem de erro
                    erro.value?.let { errorMsg ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMsg,
                            color = ErrorRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // =============== BOTÕES SOCIAIS ===============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Login Google */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "G",
                                    color = Color(0xFFDB4437),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Google",
                                    color = TextDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { /* Login Facebook */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "f",
                                    color = Color(0xFF1877F2),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Facebook",
                                    color = TextDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Link de cadastro
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Não tem uma conta? ",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Cadastre-se",
                            color = PrimaryPurple,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navegacao?.navigate("cadastro")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// =============== ELEMENTOS DECORATIVOS FLUTUANTES ===============
@Composable
private fun FloatingElements(animOffset: Float, bounce: Float) {
    // Planeta laranja
    Box(
        modifier = Modifier
            .offset(x = 40.dp, y = (50 + bounce).dp)
            .size(45.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFB347),
                        Color(0xFFFF8C42)
                    )
                ),
                CircleShape
            )
    )

    // Anel do planeta
    Box(
        modifier = Modifier
            .offset(x = 32.dp, y = (68 + bounce).dp)
            .size(60.dp, 10.dp)
            .background(
                Color(0xFFFFD89B).copy(alpha = 0.5f),
                RoundedCornerShape(5.dp)
            )
    )

    // Lua azul
    Box(
        modifier = Modifier
            .offset(x = 320.dp, y = (80 + bounce * 0.6f).dp)
            .size(22.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF4F46E5)
                    )
                ),
                CircleShape
            )
    )

    // Estrelas
    listOf(
        Triple(160.dp, 45.dp, 6.dp),
        Triple(280.dp, 120.dp, 5.dp),
        Triple(60.dp, 140.dp, 4.dp),
        Triple(220.dp, 60.dp, 5.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
        )
    }

    // Cruz decorativa
    Canvas(
        modifier = Modifier
            .offset(x = 130.dp, y = 150.dp)
            .size(14.dp)
    ) {
        val strokeWidth = 2.dp.toPx()
        val center = size.width / 2
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(center, 0f),
            end = Offset(center, size.height),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(0f, center),
            end = Offset(size.width, center),
            strokeWidth = strokeWidth
        )
    }

    // Triângulo vermelho
    Canvas(
        modifier = Modifier
            .offset(x = 300.dp, y = 40.dp)
            .size(18.dp)
            .graphicsLayer { rotationZ = animOffset * 0.05f }
    ) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width / 2, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = path,
            color = Color(0xFFFF6B6B).copy(alpha = 0.8f)
        )
    }

    // Círculo vazado
    Box(
        modifier = Modifier
            .offset(x = 340.dp, y = 160.dp)
            .size(14.dp)
            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
    )

    // Quadrado rotacionado
    Box(
        modifier = Modifier
            .offset(x = 20.dp, y = (180 + bounce * 0.5f).dp)
            .size(12.dp)
            .graphicsLayer { rotationZ = 45f }
            .background(Color(0xFFFF6B6B).copy(alpha = 0.6f), RoundedCornerShape(2.dp))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    Login(navegacao = null)
}