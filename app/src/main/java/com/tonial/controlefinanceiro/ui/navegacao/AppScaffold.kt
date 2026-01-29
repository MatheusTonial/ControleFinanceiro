package com.tonial.controlefinanceiro.ui.navegacao

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.model.ContaRecorrenteViewModel
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.dashboard.DashboardScreen
import com.tonial.controlefinanceiro.ui.historico.HistoricoScreen
import com.tonial.controlefinanceiro.ui.splash.SplashScreen
import com.tonial.controlefinanceiro.ui.telas.ListarCategoriasScreen
import com.tonial.controlefinanceiro.ui.telas.TelaCategoria
import com.tonial.controlefinanceiro.ui.telas.TelaContaRecorrente
import com.tonial.controlefinanceiro.ui.telas.TelaLancamentoConta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val LANCAMENTO_CONTA = "lancamento_conta"
    const val CADASTRO_CATEGORIA = "cadastro_categoria"
    const val HISTORICO = "historico"
    const val LISTA_CATEGORIA = "lista_categoria"
    const val CONTA_RECORRENTE = "conta_recorrente"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(startDestination: String = Routes.SPLASH) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val banco = remember { DatabaseHandler.getInstance(context) }
    val viewModel: FluxoViewModel = viewModel()
    val contaRecorrenteViewModel: ContaRecorrenteViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSplashScreen = currentRoute == Routes.SPLASH

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { uri ->
            scope.launch {
                val message = try {
                    withContext(Dispatchers.IO) {
                        val dbFile = context.getDatabasePath(DatabaseHandler.DATABASE_NAME)
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            dbFile.inputStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        "Backup realizado com sucesso!"
                    }
                } catch (e: Exception) {
                    "Falha ao realizar o backup: ${e.message}"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        uri?.let {
            scope.launch {
                val message = try {
                    withContext(Dispatchers.IO) {
                        banco.close()
                        val dbFile = context.getDatabasePath(DatabaseHandler.DATABASE_NAME)
                        context.contentResolver.openInputStream(it)?.use { inputStream ->
                            dbFile.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        "Backup restaurado com sucesso! O aplicativo será reiniciado."
                    }
                } catch (e: Exception) {
                    "Falha ao restaurar o backup: ${e.message}"
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                if (message.startsWith("Backup restaurado com sucesso")) {
                    val packageManager = context.packageManager
                    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                    val componentName = intent!!.component
                    val mainIntent = Intent.makeRestartActivityTask(componentName)
                    context.startActivity(mainIntent)
                    exitProcess(0)
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isSplashScreen,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "📈 Dashboard") },
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = {
                        navController.navigate(Routes.DASHBOARD) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "📆 Histórico de lançamentos") },
                    selected = currentRoute == Routes.HISTORICO,
                    onClick = {
                        navController.navigate(Routes.HISTORICO) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "➕ Lançamento de Conta") },
                    selected = currentRoute?.startsWith(Routes.LANCAMENTO_CONTA) == true,
                    onClick = {
                        navController.navigate(Routes.LANCAMENTO_CONTA) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "📃 Cadastro de Categoria") },
                    selected = currentRoute == Routes.CADASTRO_CATEGORIA,
                    onClick = {
                        navController.navigate(Routes.CADASTRO_CATEGORIA) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "📃 Lista de Categoria") },
                    selected = currentRoute == Routes.LISTA_CATEGORIA,
                    onClick = {
                        navController.navigate(Routes.LISTA_CATEGORIA) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "🕐 Cadastro de Conta Recorrente") },
                    selected = currentRoute == Routes.CONTA_RECORRENTE,
                    onClick = {
                        navController.navigate(Routes.CONTA_RECORRENTE) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text(text = "📥 Importar Banco de Dados") },
                    selected = false,
                    onClick = {
                        restoreLauncher.launch(arrayOf("application/octet-stream"))
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "📤 Exportar Banco de Dados") },
                    selected = false,
                    onClick = {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream"
                            putExtra(Intent.EXTRA_TITLE, "backup.sqlite")
                        }
                        backupLauncher.launch(intent)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) { 
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Routes.SPLASH) {
                SplashScreen(onTimeout = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    drawerState = drawerState,
                    onNavigateToLancamento = {
                        val route = if (it != null) "${Routes.LANCAMENTO_CONTA}?lancamentoId=$it" else Routes.LANCAMENTO_CONTA
                        navController.navigate(route)
                    }
                )
            }
            composable(Routes.HISTORICO) {
                HistoricoScreen(
                    drawerState = drawerState,
                    onNavigateToLancamento = {
                        val route = if (it != null) "${Routes.LANCAMENTO_CONTA}?lancamentoId=$it" else Routes.LANCAMENTO_CONTA
                        navController.navigate(route)
                    }
                )
            }
            composable(
                route = "${Routes.LANCAMENTO_CONTA}?lancamentoId={lancamentoId}",
                arguments = listOf(navArgument("lancamentoId") { 
                    type = NavType.StringType
                    nullable = true 
                })
            ) {
                var categorias by remember { mutableStateOf<List<Categorias>>(emptyList()) }
                val lancamentoId = it.arguments?.getString("lancamentoId")
                val isOpenedFromWidget = startDestination == Routes.LANCAMENTO_CONTA

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        categorias = banco.getAllCategorias()
                    }
                }

                TelaLancamentoConta(
                    viewModel = viewModel,
                    categorias = categorias,
                    onSaveClick = {
                        if (viewModel.salvarConta()) {
                            Toast.makeText(context, "Conta salva com sucesso!", Toast.LENGTH_SHORT).show()
                            if (isOpenedFromWidget) {
                                navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.LANCAMENTO_CONTA) { inclusive = true } }
                            } else {
                                navController.popBackStack()
                            }
                        } else {
                            viewModel.mensagemErro?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onBackClick = {
                         if (isOpenedFromWidget) {
                            navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.LANCAMENTO_CONTA) { inclusive = true } }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    lancamentoId = lancamentoId?.toLongOrNull(),
                    isOpenedFromWidget = isOpenedFromWidget
                )
            }
            composable(
                route = "${Routes.CONTA_RECORRENTE}?lancamentoId={lancamentoId}",
                arguments = listOf(navArgument("lancamentoId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                var categorias by remember { mutableStateOf<List<Categorias>>(emptyList()) }
                val lancamentoId = it.arguments?.getString("lancamentoId")

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        categorias = banco.getAllCategorias()
                    }
                }

                TelaContaRecorrente(
                    viewModel = contaRecorrenteViewModel,
                    categorias = categorias,
                    onSaveClick = {
                        if (contaRecorrenteViewModel.salvarConta()) {
                            Toast.makeText(context, "Conta recorrente salva com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            contaRecorrenteViewModel.mensagemErro?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                    lancamentoId = lancamentoId?.toLongOrNull()
                )
            }
            composable(
                route = "${Routes.CADASTRO_CATEGORIA}?categoriaId={categoriaId}",
                arguments = listOf(navArgument("categoriaId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val categoriaId = it.arguments?.getString("categoriaId")
                TelaCategoria(
                    viewModel = viewModel,
                    onSaveClick = {
                        viewModel.salvarCategoria()
                        Toast.makeText(context, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() },
                    categoriaId = categoriaId?.toLongOrNull()
                )
            }
            composable(Routes.LISTA_CATEGORIA) {
                ListarCategoriasScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToCategoria = {
                        val route = if (it != null) "${Routes.CADASTRO_CATEGORIA}?categoriaId=$it" else Routes.CADASTRO_CATEGORIA
                        navController.navigate(route)
                    }
                )
            }
        }
    }
}