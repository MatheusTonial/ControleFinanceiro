package com.tonial.controlefinanceiro.ui.navegacao

import android.widget.Toast
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
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.dashboard.DashboardScreen
import com.tonial.controlefinanceiro.ui.historico.HistoricoScreen
import com.tonial.controlefinanceiro.ui.splash.SplashScreen
import com.tonial.controlefinanceiro.ui.telas.TelaCategoria
import com.tonial.controlefinanceiro.ui.telas.TelaLancamentoConta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val LANCAMENTO_CONTA = "lancamento_conta"
    const val CADASTRO_CATEGORIA = "cadastro_categoria"
    const val HISTORICO = "historico"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val banco = remember { DatabaseHandler.getInstance(context) }
    val viewModel: FluxoViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSplashScreen = currentRoute == Routes.SPLASH

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isSplashScreen,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Dashboard") },
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = {
                        navController.navigate(Routes.DASHBOARD) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Histórico de lançamentos") },
                    selected = currentRoute == Routes.HISTORICO,
                    onClick = {
                        navController.navigate(Routes.HISTORICO) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Lançamento de Conta") },
                    selected = currentRoute?.startsWith(Routes.LANCAMENTO_CONTA) == true,
                    onClick = {
                        navController.navigate(Routes.LANCAMENTO_CONTA) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Cadastro de Categoria") },
                    selected = currentRoute == Routes.CADASTRO_CATEGORIA,
                    onClick = {
                        navController.navigate(Routes.CADASTRO_CATEGORIA) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) { 
        NavHost(navController = navController, startDestination = Routes.SPLASH) {
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
                            navController.popBackStack()
                        } else {
                            viewModel.mensagemErro?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                    lancamentoId = lancamentoId?.toLongOrNull()
                )
            }
            composable(Routes.CADASTRO_CATEGORIA) {
                TelaCategoria(
                    viewModel = viewModel,
                    onSaveClick = {
                        viewModel.salvarCategoria()
                        Toast.makeText(context, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}