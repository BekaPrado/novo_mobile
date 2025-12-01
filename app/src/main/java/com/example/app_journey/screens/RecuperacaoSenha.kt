package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R
import com.example.app_journey.model.EmailRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.service.RetrofitEmailFactory

@Composable
fun RecuperacaoSenha(navegacao: NavHostController?) {
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val mensagem = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(
                colors = listOf(Color(0xff39249D), Color(0xff180D5b))
            ))
    ) {
        Column (
            modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
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

                    Button(

                        onClick = { navegacao?.navigate("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.width(150.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(90.dp)
                                .width(230.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))


                    Text(
                        text = "Recuperar senha",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        textAlign = TextAlign.Center,
                        text = "Digite seu email para podermos te enviar o código de recuperação.",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text(text = "E-mail", color = Color.White) },
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.value.text.isBlank()) {
                                mensagem.value = "Preencha o e-mail"
                                return@Button
                            }

                            loading.value = true
                            mensagem.value = null

                            val requisicao = EmailRequest(
                                email = email.value.text
                            )


                            RetrofitEmailFactory.getEmailService().enviarEmail(requisicao)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        loading.value = false
                                        if (response.isSuccessful) {
                                            val emailEncoded = Uri.encode(email.value.text)
                                            navegacao?.navigate("verificar_email/$emailEncoded")
                                        } else {
                                            Log.e("EmailErro", "Erro HTTP ${response.code()}")
                                            mensagem.value =
                                                "Erro ao enviar código ao e-mail. ERRO: ${response.code()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        loading.value = false
                                        Log.e("EmailErro", "Erro de rede", t)
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
                            text = if (loading.value) "Recuperando..." else "Recuperar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xff341E9B)
                        )
                    }

                    mensagem.value?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(it, color = if (it.contains("Erro")) Color.Red else Color(0xFF037EF7))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecuperacaoSenhaPreview() {
    val fakeNavController = rememberNavController()
    RecuperacaoSenha(navegacao = fakeNavController)
}