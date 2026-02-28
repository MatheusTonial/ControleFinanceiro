package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SobreScreen(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    // Obtém o contexto para acessar as informações do pacote.
    val context = LocalContext.current
    // Busca o nome da versão do app dinamicamente.
    val versionName = try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (e: Exception) {
        "0.1" // Valor padrão em caso de erro (útil para previews).
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Coluna principal que organiza a tela verticalmente, corrigindo a sobreposição.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Padding geral para a tela
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Cabeçalho com o nome do app e a descrição.
            Text(
                text = "Controle Financeiro",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Este aplicativo foi desenvolvido para ajudar você a gerenciar suas finanças de forma simples e eficaz.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Corpo com notas de versão (rolável)
            LazyColumn(
                // O weight(1f) faz com que a LazyColumn ocupe todo o espaço disponível, empurrando o rodapé para baixo.
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text("Notas de Versão:", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    Text("v7.0 - Adicionado atalho no painel de configurções rapidas", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v6.5 - Janela de aviso quando faz mais de 30 dias desde o ultimo backup.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v6.0 - Acesso para realizar backup e restaurar banco de dados.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v5.0 - Flag de lançamento único, identifica gastos exporadicos para não afetar os calculos de comparativo do mês anterior e projeção.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v4.5 - Identificação por cor das contas recorrentes que estão vencidas ou ja foram lançadas.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v4.0 - Contas recorrentes, contas que são repedidas e podem ser pré cadastrada para lançamento rapido; contas do mes atual que ainda não foram lançadas são contabilizadas na projeção.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v3.5 - Widget para cadastro rapido.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v3.0 - Tela de histórico de lançamentos; Filtros de data e categoria.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v2.0 - Cadastro de categorias; lista de top 5 categorias.", modifier = Modifier.padding(vertical = 4.dp))
                }
                item {
                    Text("v1.0 - Lançamento inicial do aplicativo.", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Rodapé com a versão e o desenvolvedor.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Exibe a versão do app dinamicamente.
                Text(text = "Versão $versionName")
                Text(text = "Desenvolvido por toni_al 🥔")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SobreScreenPreview() {
    ControleFinanceiroTheme {
        SobreScreen(drawerState = rememberDrawerState(initialValue = DrawerValue.Closed))
    }
}
