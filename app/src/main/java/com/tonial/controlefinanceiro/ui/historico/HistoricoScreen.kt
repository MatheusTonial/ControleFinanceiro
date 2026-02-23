package com.tonial.controlefinanceiro.ui.historico

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import com.tonial.controlefinanceiro.ui.dashboard.LancamentoRow
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Composable principal para a tela de Histórico.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    drawerState: DrawerState,
    onNavigateToLancamento: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricoViewModel = viewModel(),
) {
    // Coleta os estados do ViewModel.
    val historico by viewModel.historico.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val scope = rememberCoroutineScope()

    // Estados para os filtros da tela.
    var dataInicio by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var dataFim by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategoria by remember { mutableStateOf<Categorias?>(null) }

    // Estados para o diálogo de exclusão.
    var showDeleteDialog by remember { mutableStateOf(false) }
    var lancamentoToDelete by remember { mutableStateOf<UltimoLancamento?>(null) }

    // Carrega as categorias ao iniciar a tela.
    LaunchedEffect(Unit) {
        viewModel.loadCategorias()
    }

    // Recarrega o histórico sempre que um filtro é alterado.
    LaunchedEffect(dataInicio, dataFim, selectedCategoria) {
        viewModel.loadHistorico(dataInicio, dataFim, selectedCategoria?._id)
    }

    // Diálogo de confirmação para exclusão de lançamento.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Lançamento") },
            text = { Text("Tem certeza que deseja excluir este lançamento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        lancamentoToDelete?.let { viewModel.deleteLancamento(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Estrutura principal da tela.
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        // O conteúdo da tela agora é diretamente renderizado dentro do Scaffold.
        HistoricoContent(
            modifier = Modifier.padding(paddingValues),
            historico = historico,
            categorias = categorias,
            dataInicio = dataInicio,
            dataFim = dataFim,
            selectedCategoria = selectedCategoria,
            onDataInicioChange = { dataInicio = it },
            onDataFimChange = { dataFim = it },
            onCategoriaChange = { selectedCategoria = it },
            onEdit = { onNavigateToLancamento(it._id.toString()) },
            onDelete = {
                lancamentoToDelete = it
                showDeleteDialog = true
            }
        )
    }
}

// Composable para o conteúdo principal da tela, incluindo filtros e a lista.
@Composable
fun HistoricoContent(
    modifier: Modifier = Modifier,
    historico: List<UltimoLancamento>,
    categorias: List<Categorias>,
    dataInicio: LocalDate,
    dataFim: LocalDate,
    selectedCategoria: Categorias?,
    onDataInicioChange: (LocalDate) -> Unit,
    onDataFimChange: (LocalDate) -> Unit,
    onCategoriaChange: (Categorias?) -> Unit,
    onEdit: (UltimoLancamento) -> Unit,
    onDelete: (UltimoLancamento) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // O histórico é passado para os filtros para que o totalizador possa ser calculado.
        Filtros(
            historico = historico,
            categorias = categorias,
            dataInicio = dataInicio,
            dataFim = dataFim,
            selectedCategoria = selectedCategoria,
            onDataInicioChange = onDataInicioChange,
            onDataFimChange = onDataFimChange,
            onCategoriaChange = onCategoriaChange
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historico) { lancamento ->
                LancamentoRow(lancamento = lancamento, onEdit = onEdit, onDelete = onDelete)
            }
        }
    }
}

// Composable para o card que exibe o saldo do período filtrado. Agora é mais compacto.
@Composable
fun TotalizadorHistorico(
    historico: List<UltimoLancamento>,
    modifier: Modifier = Modifier
) {
    // Calcula o saldo total (ganhos - perdas) dos lançamentos na lista.
    val total = historico.fold(BigDecimal.ZERO) { acc, lancamento ->
        if (lancamento.tipo == TipoCategoria.Ganho.name) {
            acc + lancamento.valor
        } else {
            acc - lancamento.valor
        }
    }

    // Formata o valor total como moeda brasileira.
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val valorFormatado = numberFormat.format(total)

    // Determina a cor do texto com base se o valor é positivo ou negativo.
    val cor = if (total >= BigDecimal.ZERO) Color(0xFF388E3C) else MaterialTheme.colorScheme.error

    // Card para exibir o totalizador. O padding e a elevação foram reduzidos.
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text = "Saldo do Período", style = MaterialTheme.typography.labelSmall)
            Text(
                text = valorFormatado,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = cor
            )
        }
    }
}

// Composable que agrupa os componentes de filtro.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filtros(
    historico: List<UltimoLancamento>,
    categorias: List<Categorias>,
    dataInicio: LocalDate,
    dataFim: LocalDate,
    selectedCategoria: Categorias?,
    onDataInicioChange: (LocalDate) -> Unit,
    onDataFimChange: (LocalDate) -> Unit,
    onCategoriaChange: (Categorias?) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val showDatePickerInicio = remember { mutableStateOf(false) }
    val showDatePickerFim = remember { mutableStateOf(false) }

    // Agrupa todos os filtros em uma única coluna.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // Linha 1: Campos de texto para seleção de data de início e fim.
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = dataInicio.format(formatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Início") },
                trailingIcon = {
                    IconButton(onClick = { showDatePickerInicio.value = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Data Início")
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = dataFim.format(formatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fim") },
                trailingIcon = {
                    IconButton(onClick = { showDatePickerFim.value = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Data Fim")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Diálogo para selecionar a data de início.
        if (showDatePickerInicio.value) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dataInicio.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePickerInicio.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                onDataInicioChange(selectedDate)
                            }
                            showDatePickerInicio.value = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerInicio.value = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Diálogo para selecionar a data de fim.
        if (showDatePickerFim.value) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dataFim.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePickerFim.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                onDataFimChange(selectedDate)
                            }
                            showDatePickerFim.value = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerFim.value = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Linha 2: Saldo do período e filtro de categoria lado a lado.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coluna da esquerda: Totalizador do saldo. O peso foi ajustado para 1, dando menos espaço.
            Box(modifier = Modifier.weight(1f)) {
                TotalizadorHistorico(historico = historico, modifier = Modifier.fillMaxWidth())
            }

            // Coluna da direita: Menu dropdown para selecionar a categoria. O peso foi ajustado para 1.5, dando mais espaço.
            Box(modifier = Modifier.weight(1.5f)) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategoria?.descricao ?: "Todas",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas as categorias") },
                            onClick = {
                                onCategoriaChange(null)
                                expanded = false
                            }
                        )
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.descricao) },
                                onClick = {
                                    onCategoriaChange(categoria)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Linha 3: Botões para filtros rápidos de período.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val mesAnterior = LocalDate.now().minusMonths(1)
                    onDataInicioChange(mesAnterior.withDayOfMonth(1))
                    onDataFimChange(mesAnterior.withDayOfMonth(mesAnterior.lengthOfMonth()))
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text("Mês anterior")
            }
            Button(
                onClick = {
                    val hoje = LocalDate.now()
                    onDataInicioChange(hoje.withDayOfMonth(1))
                    onDataFimChange(hoje.plusMonths(1).withDayOfMonth(1).minusDays(1))
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text("Mês atual")
            }
            Button(
                onClick = {
                    val hoje = LocalDate.now()
                    onDataInicioChange(hoje.minusDays(6))
                    onDataFimChange(hoje)
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text("7 dias")
            }
        }
    }
}

// Preview da tela para desenvolvimento.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoricoScreenPreview() {
    ControleFinanceiroTheme {
        val mockLancamentos = listOf(
            UltimoLancamento(1,"Compra no mercado", "Mercado", BigDecimal("150.20"), LocalDate.now().toString(), TipoCategoria.Perda.name),
            UltimoLancamento(2,"Cinema", "Lazer", BigDecimal("80.00"), LocalDate.now().minusDays(1).toString(), TipoCategoria.Perda.name),
            UltimoLancamento(3,"Salário", "Salário", BigDecimal("5000.00"), LocalDate.now().minusDays(2).toString(), TipoCategoria.Ganho.name),
            UltimoLancamento(4,"Posto Shell", "Transporte", BigDecimal("150.00"), LocalDate.now().minusDays(3).toString(), TipoCategoria.Perda.name)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Histórico") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            HistoricoContent(
                modifier = Modifier.padding(paddingValues),
                historico = mockLancamentos,
                categorias = emptyList(),
                dataInicio = LocalDate.now().withDayOfMonth(1),
                dataFim = LocalDate.now(),
                selectedCategoria = null,
                onDataInicioChange = {},
                onDataFimChange = {},
                onCategoriaChange = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}
