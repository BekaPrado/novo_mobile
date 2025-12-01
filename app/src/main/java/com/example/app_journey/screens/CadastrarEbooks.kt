package com.example.app_journey.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.R
import com.example.app_journey.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastrarEbookScreen(
    onCancelar: () -> Unit,
    onPublicar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf("Ficção", "Romance", "Tecnologia", "Educação")

    // Launchers para arquivos
    var uriCapa by remember { mutableStateOf<String?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uriCapa = uri?.lastPathSegment }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar e-book", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPrimaryPurple)
            )
        },
        containerColor = LightAccent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título do e-book") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.6f),
                    focusedContainerColor = caixaC,
                    unfocusedContainerColor = caixaP
                )
            )
            Spacer(Modifier.height(12.dp))

            // Categoria Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria/Gênero") },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryPurple) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.6f),
                        focusedContainerColor = caixaC,
                        unfocusedContainerColor = caixaP
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = PrimaryPurple) },
                            onClick = {
                                categoria = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Preço
            OutlinedTextField(
                value = preco,
                onValueChange = { preco = it },
                label = { Text("Preço") },
                placeholder = { Text("R$00,00") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.6f),
                    focusedContainerColor = caixaC,
                    unfocusedContainerColor = caixaP
                )
            )
            Spacer(Modifier.height(12.dp))

            // Descrição
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.6f),
                    focusedContainerColor = caixaC,
                    unfocusedContainerColor = caixaP
                )
            )
            Spacer(Modifier.height(16.dp))

            // Selecionar Capa
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = caixaP),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryPurple)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = uriCapa ?: "Selecionar Capa",
                    color = PrimaryPurple
                )
            }

            Spacer(Modifier.height(12.dp))

            // Arquivo do E-book
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Selecionar Arquivo") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = PrimaryPurple.copy(alpha = 0.6f),
                    focusedContainerColor = caixaC,
                    unfocusedContainerColor = caixaP
                )
            )

            Spacer(Modifier.height(24.dp))

            // Botões Cancelar / Publicar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancelar,
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar", color = PrimaryPurple)
                }
                Button(
                    onClick = onPublicar,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Publicar", color = White)
                }
            }
        }
    }
}
