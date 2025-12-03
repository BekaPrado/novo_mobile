package com.example.app_journey.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// =============== PALETA DE CORES ===============
private val PrimaryPurple = Color(0xFF6C5CE7)
private val PrimaryPurpleDark = Color(0xFF5849C2)
private val AccentPurple = Color(0xFF8B7CF7)
private val SoftPurple = Color(0xFFE8E5FF)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A2E)
private val AccentGreen = Color(0xFF10B981)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentOrange = Color(0xFFFF9F43)
private val AccentRed = Color(0xFFEF4444)
private val AccentPink = Color(0xFFEC4899)
private val AccentTeal = Color(0xFF14B8A6)
private val AccentYellow = Color(0xFFF59E0B)

@Composable
fun DrawerMenu(
    idUsuario: Int,
    nomeUsuario: String = "Usuário",
    emailUsuario: String = "usuario@email.com",
    fotoUsuario: String? = null,
    onOptionSelected: (String) -> Unit,
    onCloseDrawer: () -> Unit = {}
) {
    // Animações
    val infiniteTransition = rememberInfiniteTransition(label = "drawer")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // =============== BACKGROUND GRADIENTE ===============
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryPurple,
                                PrimaryPurpleDark,
                                PrimaryPurple.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // =============== ELEMENTOS DECORATIVOS ===============
            DrawerFloatingElements(glowAnim)

            // =============== CONTEÚDO ===============
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // =============== HEADER DO PERFIL ===============
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable {
                            onOptionSelected("perfil/$idUsuario")
                            onCloseDrawer()
                        }
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Foto do perfil
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (fotoUsuario != null) {
                                AsyncImage(
                                    model = fotoUsuario,
                                    contentDescription = "Foto",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    text = nomeUsuario.firstOrNull()?.uppercase() ?: "U",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nomeUsuario,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = emailUsuario,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Badge "Ver perfil"
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Ver perfil →",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // =============== SEÇÃO PRINCIPAL ===============
                DrawerSectionTitle("PRINCIPAL")

                DrawerMenuItem(
                    text = "Início",
                    icon = Icons.Default.Home,
                    iconColor = AccentBlue,
                    onClick = {
                        onOptionSelected("home/$idUsuario")
                        onCloseDrawer()
                    }
                )

                DrawerMenuItem(
                    text = "Criar Grupo",
                    icon = Icons.Default.Add,
                    iconColor = AccentGreen,
                    onClick = {
                        onOptionSelected("criar_grupo")
                        onCloseDrawer()
                    }
                )

                DrawerMenuItem(
                    text = "Meus Grupos",
                    icon = Icons.Default.List,
                    iconColor = AccentPurple,
                    onClick = {
                        onOptionSelected("meus_grupos/$idUsuario")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =============== SEÇÃO CALENDÁRIO ===============
                DrawerSectionTitle("AGENDA")

                DrawerMenuItem(
                    text = "Meu Calendário",
                    icon = Icons.Default.DateRange,
                    iconColor = AccentOrange,
                    onClick = {
                        onOptionSelected("calendario_pessoal/$idUsuario")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =============== SEÇÃO COMUNICAÇÃO ===============
                DrawerSectionTitle("COMUNICAÇÃO")

                DrawerMenuItem(
                    text = "Conversas Privadas",
                    icon = Icons.Default.Email,
                    iconColor = AccentPink,
                    badgeCount = 3, // Exemplo de notificação
                    onClick = {
                        onOptionSelected("conversasPrivadas/$idUsuario")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =============== SEÇÃO RECURSOS ===============
                DrawerSectionTitle("RECURSOS")

                DrawerMenuItem(
                    text = "E-Books",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = AccentTeal,
                    onClick = {
                        onOptionSelected("ebooks")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =============== SEÇÃO CONTA ===============
                DrawerSectionTitle("CONTA")

                DrawerMenuItem(
                    text = "Meu Perfil",
                    icon = Icons.Default.Person,
                    iconColor = Color.White,
                    onClick = {
                        onOptionSelected("perfil/$idUsuario")
                        onCloseDrawer()
                    }
                )

                DrawerMenuItem(
                    text = "Editar Perfil",
                    icon = Icons.Default.Edit,
                    iconColor = AccentYellow,
                    onClick = {
                        onOptionSelected("editar_info/$idUsuario")
                        onCloseDrawer()
                    }
                )

                DrawerMenuItem(
                    text = "Configurações",
                    icon = Icons.Default.Settings,
                    iconColor = Color.White.copy(alpha = 0.8f),
                    onClick = {
                        onOptionSelected("configuracoes")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // =============== DIVISOR ===============
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // =============== SEÇÃO PERIGO ===============
                DrawerMenuItem(
                    text = "Sair da Conta",
                    icon = Icons.Default.ExitToApp,
                    iconColor = AccentOrange,
                    textColor = AccentOrange,
                    onClick = {
                        onOptionSelected("logout")
                        onCloseDrawer()
                    }
                )

                DrawerMenuItem(
                    text = "Apagar Perfil",
                    icon = Icons.Default.Delete,
                    iconColor = AccentRed,
                    textColor = AccentRed,
                    onClick = {
                        onOptionSelected("apagar_perfil")
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Versão do app
                Text(
                    text = "Journey v1.0.0",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// =============== TÍTULO DA SEÇÃO ===============
@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.5f),
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )
}

// =============== ELEMENTOS DECORATIVOS ===============
@Composable
private fun DrawerFloatingElements(glow: Float) {
    Box(
        modifier = Modifier
            .offset(x = 220.dp, y = 100.dp)
            .size(100.dp)
            .blur(40.dp)
            .background(
                Color.White.copy(alpha = glow * 0.08f),
                CircleShape
            )
    )

    Box(
        modifier = Modifier
            .offset(x = (-30).dp, y = 300.dp)
            .size(80.dp)
            .blur(30.dp)
            .background(
                AccentPurple.copy(alpha = glow * 0.15f),
                CircleShape
            )
    )

    Box(
        modifier = Modifier
            .offset(x = 200.dp, y = 600.dp)
            .size(60.dp)
            .blur(25.dp)
            .background(
                Color.White.copy(alpha = glow * 0.06f),
                CircleShape
            )
    )

    listOf(
        Triple(50.dp, 180.dp, 4.dp),
        Triple(250.dp, 250.dp, 3.dp),
        Triple(30.dp, 450.dp, 3.dp),
        Triple(270.dp, 400.dp, 4.dp),
        Triple(40.dp, 650.dp, 3.dp),
    ).forEach { (x, y, size) ->
        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(size)
                .background(Color.White.copy(alpha = glow * 0.4f), CircleShape)
        )
    }
}

// =============== ITEM DO MENU ===============
@Composable
private fun DrawerMenuItem(
    text: String,
    icon: ImageVector,
    iconColor: Color = Color.White,
    textColor: Color = Color.White,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone com background
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    iconColor.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Texto
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Badge de notificação
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(AccentRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Seta
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDrawerMenu() {
    DrawerMenu(
        idUsuario = 1,
        nomeUsuario = "João Silva",
        emailUsuario = "joao@email.com",
        fotoUsuario = null,
        onOptionSelected = {},
        onCloseDrawer = {}
    )
}