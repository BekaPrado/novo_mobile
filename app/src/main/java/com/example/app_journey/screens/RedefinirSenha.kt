package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RedefinirSenha(navegacao: NavHostController?, idUsuario: Int) {
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    val mensagem = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(
                colors = listOf(Color(0xff39249D), Color(0xff180D5b))
            ))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card (
                modifier = Modifier
                    .height(560.dp)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xff351D9b))
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Troque sua senha!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                textAlign = TextAlign.Center,
                text = "Perfeito! Último passo, troque sua senha e confirme-a.",
                lineHeight = 22.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))
            Column (){

            Text("Nova Senha:", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = novaSenha,
                onValueChange = { novaSenha = it },
                placeholder = { Text("Digite sua nova senha", color = Color.White) },
                shape = RoundedCornerShape(30.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Confirmar Senha:", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                placeholder = { Text("Confirme sua senha", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(30.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
        }
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (novaSenha != confirmarSenha) {
                        mensagem.value = "As senhas não correspondem."
                        return@Button
                    }

                    if (novaSenha.isBlank()) {
                        mensagem.value = "Por favor, insira uma nova senha."
                        return@Button
                    }

                    loading.value = true
                    mensagem.value = null

                    val usuarioService = RetrofitFactory().getUsuarioService()

                    val json = JSONObject().apply {
                        put("senha", novaSenha)
                    }

                    val body = json.toString()
                        .toRequestBody("application/json".toMediaType())

                    usuarioService.redefinirSenhaRaw(idUsuario, body)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                loading.value = false
                                if (response.isSuccessful) {
                                    mensagem.value = "Senha redefinida com sucesso!"
                                    navegacao?.navigate("login")
                                } else {
                                    mensagem.value = "Erro ao redefinir senha. Código: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                loading.value = false
                                mensagem.value = "Erro de rede: ${t.message}"
                            }
                        })
                },
                shape = RoundedCornerShape(48.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (loading.value) "Alterando..." else "Alterar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xff341E9B)
                )
            }

            mensagem.value?.let {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = it,
                    color = if (it.startsWith("Senha")) Color(0xFF037EF7) else Color.Red,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RedefinirSenhaPreview() {
    RedefinirSenha(
        navegacao = null,
        idUsuario = 1
    )
}