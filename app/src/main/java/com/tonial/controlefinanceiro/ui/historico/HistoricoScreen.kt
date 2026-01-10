package com.tonial.controlefinanceiro.ui.historico

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    drawerState: DrawerState,
    onNavigateToLancamento: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoricoViewModel = viewModel(),
) {
    val historico by viewModel.historico.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val scope = rememberCoroutineScope()

    var dataInicio by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var dataFim by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategoria by remember { mutableStateOf<Categorias?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var lancamentoToDelete by remember { mutableStateOf<UltimoLancamento?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCategorias()
    }

    LaunchedEffect(dataInicio, dataFim, selectedCategoria) {
        viewModel.loadHistorico(dataInicio, dataFim, selectedCategoria?._id)
    }

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
        Filtros(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filtros(
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

    if (showDatePickerInicio.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dataInicio.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
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

    if (showDatePickerFim.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dataFim.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerFim.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
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

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategoria?.descricao ?: "Todas as categorias",
            onValueChange = { },
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
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
                onDataFimChange(hoje)
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
