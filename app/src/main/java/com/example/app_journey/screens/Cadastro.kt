@file:Suppress("UNUSED_VARIABLE")

package com.example.app_journey.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.R
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val PrimaryPurple = Color(0xFF341E9B)

@Composable
fun Cadastro(navegacao: NavHostController) {

    val nome = remember { mutableStateOf("") }
    val dataNascimento = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val confirmarSenha = remember { mutableStateOf("") }

    val context = LocalContext.current
    val erro = remember { mutableStateOf<String?>(null) }

    fun formatarDataExibicao(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length <= 2 -> digits
            digits.length <= 4 -> "${digits.take(2)}/${digits.drop(2)}"
            digits.length <= 8 -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4)}"
            else -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4).take(4)}"
        }
    }

    fun formatarDataParaIso(data: String): String {
        return try {
            val partes = data.split("/")
            "${partes[2]}-${partes[1]}-${partes[0]}"
        } catch (e: Exception) {
            data
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- TOPO COM IMAGEM (IGUAL LOGIN) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
            contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Fundo do Topo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(PrimaryPurple.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.padding(horizontal = 28.dp).padding(top = 80.dp)
            ) {
                Text(
                    text = "Crie sua\nconta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 36.sp,
                )
            }
        }
        // --- FIM TOPO ---

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(100.dp))

            // ---------- NOME ----------
            Text(
                text = "Nome completo",
                color = PrimaryPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nome.value,
                onValueChange = { nome.value = it },
                placeholder = { Text("Seu nome completo") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryPurple) },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- DATA NASCIMENTO ----------
            Text(
                text = "Data de Nascimento",
                color = PrimaryPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dataNascimento.value,
                onValueChange = { dataNascimento.value = formatarDataExibicao(it) },
                placeholder = { Text("DD/MM/AAAA") },
                leadingIcon = { Icon(Icons.Default.DateRange, null, tint = PrimaryPurple) },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- EMAIL ----------
            Text(
                text = "Email",
                color = PrimaryPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = { Text("Seu email aqui") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryPurple) },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- SENHA ----------
            Text(
                text = "Senha",
                color = PrimaryPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senha.value,
                onValueChange = { senha.value = it },
                placeholder = { Text("Sua senha") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryPurple) },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- CONFIRMAR SENHA ----------
            Text(
                text = "Confirmar senha",
                color = PrimaryPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmarSenha.value,
                onValueChange = { confirmarSenha.value = it },
                placeholder = { Text("Repita a senha") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryPurple) },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            erro.value?.let {
                Text(it, color = Color.Red, textAlign = TextAlign.Center)
            }

            // --------- BOTÃO CADASTRAR ---------
            Button(
                onClick = {

                    if (
                        nome.value.isBlank() ||
                        email.value.isBlank() ||
                        senha.value.isBlank() ||
                        confirmarSenha.value.isBlank() ||
                        dataNascimento.value.isBlank()
                    ) {
                        erro.value = "Preencha todos os campos"
                        return@Button
                    }

                    if (senha.value != confirmarSenha.value) {
                        erro.value = "As senhas não coincidem"
                        return@Button
                    }

                    val dataIso = formatarDataParaIso(dataNascimento.value)

                    val usuario = Usuario(
                        nome_completo = nome.value,
                        data_nascimento = dataIso,
                        email = email.value,
                        senha = senha.value,
                        tipo_usuario = "Estudante",
                        foto_perfil = "",
                        descricao = ""
                    )

                    RetrofitFactory().getUsuarioService()
                        .inserirUsuario(usuario)
                        .enqueue(object : Callback<Usuario> {
                            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    navegacao.navigate("login")
                                } else {
                                    erro.value = "Erro ao cadastrar: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<Usuario>, t: Throwable) {
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
                    text = "Cadastrar",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Já tem conta?", color = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Entrar",
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navegacao.navigate("login") }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CadastroPreview() {
    Cadastro(navegacao = rememberNavController())
}
