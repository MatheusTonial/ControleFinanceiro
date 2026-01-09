package com.tonial.controlefinanceiro.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val totalGastoMes by viewModel.totalGastoMes.collectAsState()
    val categoriasMaisGastas by viewModel.categoriasMaisGastas.collectAsState()
    val ultimosLancamentos by viewModel.ultimosLancamentos.collectAsState()

    viewModel.loadDashboardData()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TotalGastoCard(totalGastoMes)
        CategoriasMaisGastasCarousel(categoriasMaisGastas)
        UltimosLancamentosList(ultimosLancamentos)
    }
}

@Composable
fun TotalGastoCard(total: BigDecimal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "TOTAL GASTO NO MÊS", style = MaterialTheme.typography.titleMedium)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(total),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
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
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
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
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(lancamento.descricao, fontWeight = FontWeight.SemiBold)

                val dataFormatada = try {
                    LocalDate.parse(lancamento.data).format(DateTimeFormatter.ofPattern("dd/MM"))
                } catch (e: Exception) {
                    lancamento.data
                }

                Text("$dataFormatada • ${lancamento.categoria}", fontSize = 12.sp)
            }

            val isPerda = lancamento.tipo == TipoCategoria.Perda.name
            val cor = if (isPerda) MaterialTheme.colorScheme.error else Color(0xFF388E3C)
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            val valorFormatado = numberFormat.format(lancamento.valor)
            val textoValor = if (isPerda) "- $valorFormatado" else "+ $valorFormatado"

            Text(
                text = textoValor,
                color = cor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
