package com.tonial.controlefinanceiro.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

// Tela principal do dashboard, que gerencia o estado e a navegação.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    drawerState: DrawerState,
    onNavigateToLancamento: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    // Coleta os estados do ViewModel.
    val totalGastoMes by viewModel.totalGastoMes.collectAsState()
    val categoriasMaisGastas by viewModel.categoriasMaisGastas.collectAsState()
    val ultimosLancamentos by viewModel.ultimosLancamentos.collectAsState()
    val variacaoMes by viewModel.variacaoMes.collectAsState()
    val gastoProporcionalMesAnterior by viewModel.gastoProporcionalMesAnterior.collectAsState()
    val projecaoGastoMes by viewModel.projecaoGastoMes.collectAsState()
    val mediaDiariaAtual by viewModel.mediaDiariaAtual.collectAsState()
    val scope = rememberCoroutineScope()

    // Carrega os dados do dashboard ao iniciar a tela.
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToLancamento(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Lançamento")
            }
        }
    ) { paddingValues ->
        DashboardContent(
            modifier = Modifier.padding(paddingValues),
            totalGastoMes = totalGastoMes,
            categoriasMaisGastas = categoriasMaisGastas,
            ultimosLancamentos = ultimosLancamentos,
            variacaoMes = variacaoMes,
            gastoProporcionalMesAnterior = gastoProporcionalMesAnterior,
            projecaoGastoMes = projecaoGastoMes,
            mediaDiariaAtual = mediaDiariaAtual,
            onEdit = { onNavigateToLancamento(it._id.toString()) },
            onDelete = { viewModel.deleteLancamento(it) }
        )
    }
}

// Organiza o conteúdo do dashboard, gerenciando o estado do filtro de categoria.
@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    totalGastoMes: BigDecimal,
    categoriasMaisGastas: List<CategoriaMaisGasta>,
    ultimosLancamentos: List<UltimoLancamento>,
    variacaoMes: Double,
    gastoProporcionalMesAnterior: BigDecimal,
    projecaoGastoMes: BigDecimal,
    mediaDiariaAtual: BigDecimal,
    onEdit: (UltimoLancamento) -> Unit,
    onDelete: (UltimoLancamento) -> Unit
) {
    var selectedCategoria by remember { mutableStateOf<String?>(null) }

    val filteredLancamentos = if (selectedCategoria == null) {
        ultimosLancamentos
    } else {
        ultimosLancamentos.filter { it.categoria == selectedCategoria }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TotalGastoCard(totalGastoMes)
        // Linha para os cards de comparativo e projeção.
        // O height(IntrinsicSize.Min) garante que os cards na Row tenham a mesma altura.
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ComparativoMesCard(variacao = variacaoMes, gastoProporcionalAnterior = gastoProporcionalMesAnterior)
            }
            Box(modifier = Modifier.weight(1f)) {
                ProjecaoGastoCard(projecao = projecaoGastoMes, mediaDiaria = mediaDiariaAtual)
            }
        }
        CategoriasMaisGastasCarousel(
            categorias = categoriasMaisGastas,
            selectedCategoria = selectedCategoria,
            onCategoriaClick = { categoriaNome ->
                selectedCategoria = if (selectedCategoria == categoriaNome) null else categoriaNome
            }
        )
        UltimosLancamentosList(filteredLancamentos, onEdit, onDelete)
    }
}

// Card que exibe o total de gastos do mês.
@Composable
fun TotalGastoCard(total: BigDecimal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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

// Card para exibir o comparativo com o mês anterior.
@Composable
fun ComparativoMesCard(variacao: Double, gastoProporcionalAnterior: BigDecimal) {
    val isPositive = variacao > 0
    val cor = if (isPositive) MaterialTheme.colorScheme.error else Color(0xFF388E3C)
    val icon = if (isPositive) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    // O modificador fillMaxHeight garante que o card ocupe toda a altura definida pela Row.
    Card(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        // O verticalArrangement.SpaceBetween distribui o conteúdo verticalmente para alinhar com o card ao lado.
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "VS. MÊS ANTERIOR", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = "Variação", tint = cor, modifier = Modifier.padding(end = 4.dp))
                Text(
                    text = "${String.format("%.1f", variacao.absoluteValue)}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = cor,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = numberFormat.format(gastoProporcionalAnterior),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// Card para exibir a projeção de gastos do mês.
@Composable
fun ProjecaoGastoCard(projecao: BigDecimal, mediaDiaria: BigDecimal) {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    // O modificador fillMaxHeight garante que o card ocupe toda a altura definida pela Row.
    Card(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        // O verticalArrangement.SpaceBetween distribui o conteúdo verticalmente para alinhar com o card ao lado.
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "PROJEÇÃO MÊS", style = MaterialTheme.typography.titleSmall)
            Text(
                text = numberFormat.format(projecao),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${numberFormat.format(mediaDiaria)} / dia",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// Carrossel horizontal para as 5 categorias com mais gastos.
@Composable
fun CategoriasMaisGastasCarousel(
    categorias: List<CategoriaMaisGasta>,
    selectedCategoria: String?,
    onCategoriaClick: (String) -> Unit
) {
    Column {
        Text(text = "Top categorias do mês", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                CategoriaCard(
                    categoria = categoria,
                    isSelected = categoria.categoria == selectedCategoria,
                    onClick = { onCategoriaClick(categoria.categoria) }
                )
            }
        }
    }
}

// Card individual para cada categoria, agora com estado de seleção e clique.
@Composable
fun CategoriaCard(
    categoria: CategoriaMaisGasta,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderModifier = if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CardDefaults.shape) else Modifier

    Card(
        modifier = Modifier.clickable(onClick = onClick).then(borderModifier)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = categoria.categoria, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            val lancamentoText = if (categoria.quantidadeLancamentos == 1) "lançamento" else "lançamentos"
            Text(text = "${categoria.quantidadeLancamentos} $lancamentoText", style = MaterialTheme.typography.bodySmall)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(categoria.total),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Lista vertical com os últimos lançamentos.
@Composable
fun UltimosLancamentosList(
    lancamentos: List<UltimoLancamento>,
    onEdit: (UltimoLancamento) -> Unit,
    onDelete: (UltimoLancamento) -> Unit
) {
    Column {
        Text(text = "Últimos Lançamentos", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lancamentos) { lancamento ->
                LancamentoRow(lancamento, onEdit, onDelete)
            }
        }
    }
}

// Componente para uma única linha da lista de lançamentos.
@Composable
fun LancamentoRow(
    lancamento: UltimoLancamento,
    onEdit: (UltimoLancamento) -> Unit,
    onDelete: (UltimoLancamento) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = textoValor, color = cor, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 8.dp))
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Editar") }, onClick = { onEdit(lancamento); expanded = false })
                        DropdownMenuItem(text = { Text("Excluir") }, onClick = { onDelete(lancamento); expanded = false })
                    }
                }
            }
        }
    }
}

// Preview da tela do dashboard para desenvolvimento.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenPreview() {
    ControleFinanceiroTheme {
        val mockCategorias = listOf(
            CategoriaMaisGasta("Mercado", BigDecimal("550.75"), 5),
            CategoriaMaisGasta("Aluguel", BigDecimal("1200.00"), 1),
            CategoriaMaisGasta("Lazer", BigDecimal("250.00"), 3),
            CategoriaMaisGasta("Transporte", BigDecimal("150.00"), 2),
            CategoriaMaisGasta("Saúde", BigDecimal("300.00"), 1)
        )
        val mockLancamentos = listOf(
            UltimoLancamento(1, "Compra no mercado", "Mercado", BigDecimal("150.20"), LocalDate.now().toString(), TipoCategoria.Perda.name),
            UltimoLancamento(2, "Cinema", "Lazer", BigDecimal("80.00"), LocalDate.now().minusDays(1).toString(), TipoCategoria.Perda.name),
            UltimoLancamento(3, "Salário", "Salário", BigDecimal("5000.00"), LocalDate.now().minusDays(2).toString(), TipoCategoria.Ganho.name),
            UltimoLancamento(4, "Posto Shell", "Transporte", BigDecimal("150.00"), LocalDate.now().minusDays(3).toString(), TipoCategoria.Perda.name)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, "Adicionar")
                }
            }
        ) { paddingValues ->
            DashboardContent(
                modifier = Modifier.padding(paddingValues),
                totalGastoMes = BigDecimal("2450.80"),
                categoriasMaisGastas = mockCategorias,
                ultimosLancamentos = mockLancamentos,
                variacaoMes = -15.5,
                gastoProporcionalMesAnterior = BigDecimal("2900.50"),
                projecaoGastoMes = BigDecimal("3100.90"),
                mediaDiariaAtual = BigDecimal("103.36"),
                onEdit = {},
                onDelete = {}
            )
        }
    }
}
