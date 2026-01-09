package com.tonial.controlefinanceiro.ui.dashboard

import android.content.res.Configuration
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Tela principal do dashboard
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val totalGastoMes by viewModel.totalGastoMes.collectAsState()
    val categoriasMaisGastas by viewModel.categoriasMaisGastas.collectAsState()
    val ultimosLancamentos by viewModel.ultimosLancamentos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    DashboardContent(
        modifier = modifier,
        totalGastoMes = totalGastoMes,
        categoriasMaisGastas = categoriasMaisGastas,
        ultimosLancamentos = ultimosLancamentos
    )
}

// Conteúdo do dashboard
@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    totalGastoMes: BigDecimal,
    categoriasMaisGastas: List<CategoriaMaisGasta>,
    ultimosLancamentos: List<UltimoLancamento>
) {
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

// Card com o total de gastos do mês
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

// Carrossel com as 5 categorias com mais gastos
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

// Card de uma categoria
@Composable
fun CategoriaCard(categoria: CategoriaMaisGasta) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nome da categoria em negrito
            Text(
                text = categoria.categoria,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            // Ajusta o plural de "lançamento"
            val lancamentoText = if (categoria.quantidadeLancamentos == 1) "lançamento" else "lançamentos"
            Text(
                text = "${categoria.quantidadeLancamentos} $lancamentoText",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(categoria.total),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Lista com os últimos lançamentos
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

// Linha de um lançamento
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

// Preview da tela de dashboard no modo noturno
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenPreview() {
    val mockCategorias = listOf(
        CategoriaMaisGasta("Mercado", BigDecimal("550.75"), 5),
        CategoriaMaisGasta("Aluguel", BigDecimal("1200.00"), 1),
        CategoriaMaisGasta("Lazer", BigDecimal("250.00"), 3),
        CategoriaMaisGasta("Transporte", BigDecimal("150.00"), 2),
        CategoriaMaisGasta("Saúde", BigDecimal("300.00"), 1)
    )
    val mockLancamentos = listOf(
        UltimoLancamento("Compra no mercado", "Mercado", BigDecimal("150.20"), LocalDate.now().toString(), TipoCategoria.Perda.name),
        UltimoLancamento("Cinema", "Lazer", BigDecimal("80.00"), LocalDate.now().minusDays(1).toString(), TipoCategoria.Perda.name),
        UltimoLancamento("Salário", "Salário", BigDecimal("5000.00"), LocalDate.now().minusDays(2).toString(), TipoCategoria.Ganho.name),
        UltimoLancamento("Posto Shell", "Transporte", BigDecimal("150.00"), LocalDate.now().minusDays(3).toString(), TipoCategoria.Perda.name)
    )

    ControleFinanceiroTheme {
        Surface {
            DashboardContent(
                totalGastoMes = BigDecimal("2450.80"),
                categoriasMaisGastas = mockCategorias,
                ultimosLancamentos = mockLancamentos
            )
        }
    }
}
