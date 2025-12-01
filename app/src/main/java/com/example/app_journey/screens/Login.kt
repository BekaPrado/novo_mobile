@file:Suppress("UNUSED_VARIABLE")

package com.example.app_journey.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R
import com.example.app_journey.model.LoginResponse
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.example.app_journey.model.LoginRequest
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val PrimaryPurple = Color(0xFF341E9B)

@Composable
fun Login(navegacao: NavHostController?) {

    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val context = LocalContext.current
    val erro = remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- Topo com Imagem de Fundo e Texto (Curvo) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
            contentAlignment = Alignment.TopStart
        ) {
            // 1. Imagem preenchendo o Box (Fundo)
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Fundo do Topo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // 2. Overlay (Opcional)
            Box(
                modifier = Modifier.matchParentSize().background(PrimaryPurple.copy(alpha = 0.3f))
            )

            // 3. Texto de Boas-Vindas
            Column(
                modifier = Modifier.padding(horizontal = 28.dp).padding(top = 80.dp)
            ) {
                Text(
                    text = "Bem-vindo\nao Journey!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 36.sp,
                )
            }
        }
        // --- Fim do Topo ---

        // --- Container Principal e Campos de Texto (ANCORADO NA PARTE INFERIOR) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // PUSHES ALL CONTENT BELOW IT TO THE BOTTOM
            Spacer(modifier = Modifier.weight(1f))

            // Espaçador para garantir que os campos fiquem abaixo da curva
            Spacer(modifier = Modifier.height(100.dp))

            // --- FORMULÁRIO ---

            // ✅ RÓTULO DO EMAIL
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    color = PrimaryPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Espaço entre o rótulo e o campo

            // EMAIL FIELD
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = { Text("Seu email aqui", color = Color.Gray) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryPurple)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    cursorColor = PrimaryPurple
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ✅ RÓTULO DA SENHA
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Senha",
                    color = PrimaryPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Espaço entre o rótulo e o campo

            // SENHA FIELD
            OutlinedTextField(
                value = senha.value,
                onValueChange = { senha.value = it },
                placeholder = { Text("Sua senha secreta", color = Color.Gray) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryPurple)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    cursorColor = PrimaryPurple
                )
            )

            // Forgot password
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text="Esqueceu sua senha?",
                    color = PrimaryPurple,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        navegacao?.navigate("recuperacao_senha")
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Erro
            erro.value?.let {
                Text(it, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // BOTÃO LOGIN
            Button(
                onClick = {

                    if (email.value.isBlank() || senha.value.isBlank()) {
                        erro.value = "Preencha todos os campos"
                        return@Button
                    }

                    val usuarioService = RetrofitFactory().getUsuarioService()
                    val loginRequest = LoginRequest(email.value, senha.value)

                    usuarioService.loginUsuario(loginRequest)
                        .enqueue(object : Callback<LoginResponse> {

                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val loginResponse = response.body()

                                    if (loginResponse != null && loginResponse.status) {

                                        erro.value = null

                                        // Salva dados no SharedPreferences
                                        loginResponse.usuario?.let { usuario ->
                                            SharedPrefHelper.salvarUsuario(context, usuario)
                                            SharedPrefHelper.salvarIdUsuario(context, usuario.id)
                                            SharedPrefHelper.salvarEmail(context, usuario.email)

                                            Log.e("Login", "ID: ${usuario.id}")
                                        }

                                        navegacao?.navigate(
                                            "home/${SharedPrefHelper.recuperarIdUsuario(context)}"
                                        ) {
                                            popUpTo("login") { inclusive = true }
                                        }

                                    } else {
                                        erro.value =
                                            loginResponse?.message ?: "Email ou senha incorretos"
                                    }

                                } else {
                                    erro.value = "Erro ao fazer login: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                erro.value = "Erro de rede: ${t.message}"
                            }
                        })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(
                    "Entrar",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("ou", color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(18.dp))

            // BOTÃO SIGN UP
            OutlinedButton(
                onClick = { navegacao?.navigate("cadastro") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple),
                border = BorderStroke(1.3.dp, PrimaryPurple)
            ) {
                Text("Criar sua conta", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            // Espaço de segurança no final
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    Login(navegacao = null)
}




