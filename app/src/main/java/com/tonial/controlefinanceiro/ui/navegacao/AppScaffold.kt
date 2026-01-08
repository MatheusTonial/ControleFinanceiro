package com.tonial.controlefinanceiro.ui.navegacao

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.dashboard.DashboardScreen
import com.tonial.controlefinanceiro.ui.telas.TelaCategoria
import com.tonial.controlefinanceiro.ui.telas.TelaLancamentoConta
import kotlinx.coroutines.launch

object Routes {
    const val DASHBOARD = "dashboard"
    const val LANCAMENTO_CONTA = "lancamento_conta"
    const val CADASTRO_CATEGORIA = "cadastro_categoria"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val banco = remember { DatabaseHandler(context) }
    val viewModel: FluxoViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        Routes.LANCAMENTO_CONTA -> "Lançamento de Conta"
        Routes.CADASTRO_CATEGORIA -> "Cadastro de Categoria"
        else -> "Dashboard"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Dashboard") },
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = { 
                        navController.navigate(Routes.DASHBOARD)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Lançamento de Conta") },
                    selected = currentRoute == Routes.LANCAMENTO_CONTA,
                    onClick = { 
                        navController.navigate(Routes.LANCAMENTO_CONTA)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Cadastro de Categoria") },
                    selected = currentRoute == Routes.CADASTRO_CATEGORIA,
                    onClick = { 
                        navController.navigate(Routes.CADASTRO_CATEGORIA)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() } 
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentRoute == Routes.DASHBOARD) {
                    FloatingActionButton(onClick = { navController.navigate(Routes.LANCAMENTO_CONTA) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Lançamento")
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController, 
                startDestination = Routes.DASHBOARD,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.DASHBOARD) {
                    DashboardScreen()
                }
                composable(Routes.LANCAMENTO_CONTA) {
                    val categorias = remember { banco.getAllCategorias() }
                    TelaLancamentoConta(
                        viewModel = viewModel,
                        categorias = categorias,
                        onSaveClick = {
                            viewModel.salvarConta(banco)
                            Toast.makeText(context, "Conta salva com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                composable(Routes.CADASTRO_CATEGORIA) {
                    TelaCategoria(
                        viewModel = viewModel,
                        onSaveClick = {
                            viewModel.salvarCategoria(banco)
                            Toast.makeText(context, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}