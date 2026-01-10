package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLancamentoConta(
    modifier: Modifier = Modifier,
    viewModel: FluxoViewModel,
    categorias: List<Categorias>,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    lancamentoId: Long? = null
) {
    LaunchedEffect(lancamentoId) {
        if (lancamentoId != null) {
            viewModel.loadConta(lancamentoId)
        } else {
            viewModel.limpaConta()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (lancamentoId == null) "Novo Lançamento" else "Editar Lançamento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        val focusRequester = remember { FocusRequester() }
        var showDatePicker by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        var selectedCategoryText by remember(viewModel.categoria_id_conta, categorias) {
            mutableStateOf(categorias.find { it._id == viewModel.categoria_id_conta }?.descricao ?: "")
        }
        var valorText by remember(viewModel.valor_conta) {
            mutableStateOf(if (viewModel.valor_conta == 0.0) "" else viewModel.valor_conta.toString())
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = viewModel.data_conta.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                viewModel.onDataContaChange(selectedDate)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Column(modifier = modifier
            .padding(paddingValues)
            .padding(8.dp)) {
            OutlinedTextField(
                value = viewModel.descricao_conta,
                onValueChange = { viewModel.onDescricaoContaChange(it) },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                OutlinedTextField(
                    value = valorText,
                    onValueChange = { newText ->
                        val sanitizedText = newText.replace(',', '.')
                        if (sanitizedText.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            valorText = sanitizedText
                            val parsedValue = sanitizedText.toDoubleOrNull()
                            if (parsedValue != null) {
                                viewModel.onValorContaChange(parsedValue)
                            } else if (sanitizedText.isEmpty()) {
                                viewModel.onValorContaChange(0.0)
                            }
                        }
                    },
                    label = { Text("R$ 0,00") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = viewModel.data_conta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = { },
                    enabled = false,
                    label = { Text("Data") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedCategoryText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ){
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.descricao) },
                            onClick = {
                                viewModel.onCategoriaIdContaChange(categoria._id)
                                selectedCategoryText = categoria.descricao
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Salvar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaLancamentoContaPreview() {
    val mockCategorias = listOf(
        Categorias(1, "Mercado", TipoCategoria.Perda, 0),
        Categorias(2, "Alimentação", TipoCategoria.Perda, 1),
        Categorias(3, "Lazer",  TipoCategoria.Perda, 5)
    )
    ControleFinanceiroTheme {
        TelaLancamentoConta(
            viewModel = viewModel(),
            categorias = mockCategorias,
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
