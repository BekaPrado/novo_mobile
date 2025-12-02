package com.example.app_journey

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.Usuario
import com.example.app_journey.screens.*
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {

    var usuarioLogado by remember { mutableStateOf<Usuario?>(null) }
    var carregandoUsuario by remember { mutableStateOf(true) }

    val idUsuarioLogado = usuarioLogado?.id_usuario ?: -1

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Carrega usuário salvo no SharedPreferences ao abrir o app
    LaunchedEffect(Unit) {
        val idSalvo = SharedPrefHelper.recuperarIdUsuario(context)
        Log.d("MainActivity", "ID salvo: $idSalvo")

        if (idSalvo != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idSalvo)
                Log.d("MainActivity", "Resposta API: $result")

                if (!result.usuario.isNullOrEmpty()) {
                    usuarioLogado = result.usuario[0]
                    Log.d("MainActivity", "Usuário carregado: ${usuarioLogado?.nome_completo}")
                } else {
                    Log.e("MainActivity", "Usuário não encontrado")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro de rede: ${e.message}")
            }
        } else {
            Log.w("MainActivity", "Nenhum id_usuario encontrado no SharedPrefs")
        }

        carregandoUsuario = false
    }

    // Observa rota atual
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route

    // Rotas que exibem AppBar + Drawer
    val rotasComBarra = listOf(
        "profile",
        "home/{idUsuario}",
        "criar_grupo",
        "editar_info/{idUsuario}",
        "meus_grupos",
        "ebooks"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                idUsuario = idUsuarioLogado,
                onOptionSelected = { rota ->
                    navController.navigate(rota)
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                if (rotaAtual in rotasComBarra) {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            if (idUsuarioLogado != -1) {
                                                navController.navigate("home/$idUsuarioLogado") {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        inclusive = false
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logoclaro),
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Text(
                                    "Journey",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                            }
                        }
                    )
                }
            }
        ) { padding ->

            NavHost(
                navController = navController,
                startDestination = "tela_inicial",
                modifier = Modifier.padding(padding)
            ) {

                // -------- Telas principais --------
                composable("tela_inicial") { TelaInicial(navController) }
                composable("login") { Login(navController) }
                composable("cadastro") { Cadastro(navController) }
                composable("recuperacao_senha") { RecuperacaoSenha(navController) }

                composable("home/{idUsuario}") { backStack ->
                    val idUsuario = backStack.arguments?.getString("idUsuario")!!.toInt()
                    Home(navController, idUsuario)
                }

                composable("profile") { Perfil(navController) }
                composable("criar_grupo") { CriarGrupo(navegacao = navController) }
                composable("meus_grupos") { MeusGrupos(navController) }

                composable("editar_info/{idUsuario}") { backStack ->
                    val idUsuario = backStack.arguments?.getString("idUsuario")?.toIntOrNull()
                    EditarInfoWrapper(navController, idUsuario)
                }

                // ------------------ Infos do grupo ------------------
                composable("grupoinfo/{id}") { entry ->
                    val grupoId = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    GrupoInfo(navController, grupoId)
                }

                composable("home_grupo/{grupoId}/{idUsuario}") { entry ->
                    val grupoId = entry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = entry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    HomeGrupo(navController, grupoId, idUsuario)
                }

                composable("calendario/{grupoId}/{idUsuario}") { entry ->
                    val grupoId = entry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = entry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    Calendario(navController, grupoId, idUsuario)
                }

                composable("meu_calendario") {
                    val idUsuario = SharedPrefHelper.recuperarIdUsuario(LocalContext.current) ?: -1
                    CalendarioPessoal(navController, idUsuario)
                }

                // ------------------ Conversas ------------------
                composable("conversasPrivadas/{idUsuario}") { entry ->
                    val idUsuario = entry.arguments?.getString("idUsuario")!!.toInt()
                    ConversasPrivadasScreen(navController, idUsuario)
                }

                composable("chatPrivado/{id}/{nome}/{idUsuario}") { entry ->
                    val chatId = entry.arguments?.getString("id")!!.toInt()
                    val nome = entry.arguments?.getString("nome")!!
                    val idUsuario = entry.arguments?.getString("idUsuario")!!.toInt()
                    ChatPrivadoScreen(navController, chatId, idUsuario, nome)
                }

                composable("chat_grupo/{grupoId}") { entry ->
                    val grupoId = entry.arguments?.getString("grupoId")!!.toInt()
                    val idUsuarioAtual = SharedPrefHelper.recuperarIdUsuario(context) ?: -1
                    ChatGrupo(navController, grupoId, idUsuarioAtual)
                }

                // ------------------ Recuperar senha ------------------
                composable("verificar_email/{email}") { entry ->
                    entry.arguments?.getString("email")?.let {
                        VerificarEmail(navController, it)
                    }
                }

                composable("redefinir_senha/{idUsuario}") { entry ->
                    entry.arguments?.getString("idUsuario")?.toIntOrNull()?.let {
                        RedefinirSenha(navController, it)
                    }
                }

                // ------------------ EBOOKS ------------------
                composable("ebooks") {
                    TelaEbooksScreen(
                        onEbookClick = { id -> navController.navigate("ebook_detalhe/$id") },
                        onCriarClick = { navController.navigate("ebook_cadastrar") },
                        onCarrinhoClick = { navController.navigate("ebook_carrinho") },
                        ebookService = RetrofitInstance.ebookService
                    )
                }

                composable("ebook_cadastrar") {
                    CadastrarEbookScreen(
                        onCancelar = { navController.popBackStack() },
                        onPublicar = { navController.navigate("ebook_confirmar_publicacao") }
                    )
                }

                composable("ebook_detalhe/{id}") { entry ->
                    val id = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    DetalheEbookScreen(
                        onAdicionarCarrinho = {
                            navController.navigate("ebook_carrinho")
                        },
                        onVoltar = { navController.popBackStack() }
                    )
                }

                composable("ebook_carrinho") {
                    CarrinhoScreen(
                        onFinalizar = {
                            navController.navigate("ebooks") {
                                popUpTo("ebooks") { inclusive = true }
                            }
                        },
                        onVoltar = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
