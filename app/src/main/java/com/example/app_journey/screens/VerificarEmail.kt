package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.R
import com.example.app_journey.model.ValidacaoResponse
import com.example.app_journey.service.RetrofitEmailFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun VerificarEmail(navegacao: NavHostController, email: String) {
    val codigo = remember { mutableStateOf("") }
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
                text = "Verifique seu E-mail",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                textAlign = TextAlign.Center,
                text = "Enviamos um código de acesso para seu email, verifique e insira os 6 dígitos.",
                lineHeight = 22.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = codigo.value,
                onValueChange = { codigo.value = it },
                label = { Text("Código:", color = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (codigo.value.isBlank()) {
                        mensagem.value = "Informe o código"
                        return@Button
                    }

                    loading.value = true
                    val request = mapOf("email" to email, "codigo" to codigo.value)

                    RetrofitEmailFactory.getEmailService().validarCodigo(request)
                        .enqueue(object : Callback<ValidacaoResponse> {
                            override fun onResponse(call: Call<ValidacaoResponse>, response: Response<ValidacaoResponse>) {
                                loading.value = false
                                if (response.isSuccessful && response.body() != null) {
                                    val idUsuario = response.body()!!.id_usuario
                                    navegacao.navigate("redefinir_senha/$idUsuario")
                                } else {
                                    mensagem.value = "Código incorreto ou expirado."
                                }
                            }

                            override fun onFailure(call: Call<ValidacaoResponse>, t: Throwable) {
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
                Text(if (loading.value) "Verificando..." else "Verificar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xff341E9B)
                )
            }

            mensagem.value?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerificarCodigoPreview() {
    val fakeNav = rememberNavController()
    VerificarEmail(
        navegacao = fakeNav,
        email = "teste@email.com"
    )
}