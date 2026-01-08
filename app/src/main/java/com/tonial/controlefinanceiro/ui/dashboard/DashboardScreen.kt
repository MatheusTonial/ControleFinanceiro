package com.tonial.controlefinanceiro.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onAddLancamento: () -> Unit
) {
    val totalGastoMes by viewModel.totalGastoMes.collectAsState()
    val categoriasMaisGastas by viewModel.categoriasMaisGastas.collectAsState()
    val ultimosLancamentos by viewModel.ultimosLancamentos.collectAsState()

    viewModel.loadDashboardData()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLancamento) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Lançamento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TotalGastoCard(totalGastoMes)
            CategoriasMaisGastasCarousel(categoriasMaisGastas)
            UltimosLancamentosList(ultimosLancamentos)
        }
    }
}

@Composable
fun TotalGastoCard(total: BigDecimal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "TOTAL GASTO NO MÊS", style = MaterialTheme.typography.titleMedium)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(total),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CategoriasMaisGastasCarousel(categorias: List<CategoriaMaisGasta>) {
    Column {
        Text(text = "Top 5 categorias", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                CategoriaCard(categoria)
            }
        }
    }
}

@Composable
fun CategoriaCard(categoria: CategoriaMaisGasta) {
    Card(
        modifier = Modifier.height(100.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = categoria.categoria, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(categoria.total),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun UltimosLancamentosList(lancamentos: List<UltimoLancamento>) {
    Column {
        Text(text = "Últimos Lançamentos", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lancamentos) { lancamento ->
                LancamentoRow(lancamento)
            }
        }
    }
}

@Composable
fun LancamentoRow(lancamento: UltimoLancamento) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = lancamento.descricao, style = MaterialTheme.typography.bodyLarge)
            Text(text = lancamento.categoria, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(lancamento.valor),
                style = MaterialTheme.typography.bodyLarge,
                color = if (lancamento.valor > BigDecimal.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
