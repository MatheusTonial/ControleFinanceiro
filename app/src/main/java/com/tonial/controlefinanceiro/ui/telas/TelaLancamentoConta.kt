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
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import java.time.Instant
import java.time.LocalDate
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
    lancamentoId: Long? = null,
    isOpenedFromWidget: Boolean = false
) {
    // Efeito para carregar ou limpar a conta com base no ID do lançamento
    LaunchedEffect(lancamentoId) {
        if (lancamentoId != null) {
            viewModel.loadConta(lancamentoId)
        } else {
            viewModel.limpaConta()
        }
    }

    // Componente de UI desacoplado que recebe estado e callbacks
    TelaLancamentoContaContent(
        modifier = modifier,
        descricao = viewModel.descricao_conta,
        onDescricaoChange = { viewModel.onDescricaoContaChange(it) },
        valor = viewModel.valor_conta,
        onValorChange = { viewModel.onValorContaChange(it) },
        data = viewModel.data_conta,
        onDataChange = { viewModel.onDataContaChange(it) },
        categoriaId = viewModel.categoria_id_conta,
        onCategoriaIdChange = { viewModel.onCategoriaIdContaChange(it) },
        categorias = categorias,
        onSaveClick = onSaveClick,
        onBackClick = onBackClick,
        isEditMode = lancamentoId != null
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLancamentoContaContent(
    modifier: Modifier = Modifier,
    descricao: String,
    onDescricaoChange: (String) -> Unit,
    valor: Double,
    onValorChange: (Double) -> Unit,
    data: LocalDate,
    onDataChange: (LocalDate) -> Unit,
    categoriaId: Long?,
    onCategoriaIdChange: (Long) -> Unit,
    categorias: List<Categorias>,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isEditMode: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (!isEditMode) "Novo Lançamento" else "Editar Lançamento") },
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

        // Texto da categoria selecionada
        var selectedCategoryText by remember(categoriaId, categorias) {
            mutableStateOf(categorias.find { it._id == categoriaId }?.descricao ?: "")
        }

        // Texto do valor, tratando o caso de ser zero
        var valorText by remember(valor) {
            mutableStateOf(if (valor == 0.0) "" else valor.toString())
        }

        // Diálogo para selecionar a data
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = data.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                onDataChange(selectedDate)
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

        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // Campo de texto para a descrição
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
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
            ) {
                // Campo de texto para o valor
                OutlinedTextField(
                    value = valorText,
                    onValueChange = { newText ->
                        val sanitizedText = newText.replace(',', '.')
                        if (sanitizedText.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            valorText = sanitizedText
                            val parsedValue = sanitizedText.toDoubleOrNull()
                            if (parsedValue != null) {
                                onValorChange(parsedValue)
                            } else if (sanitizedText.isEmpty()) {
                                onValorChange(0.0)
                            }
                        }
                    },
                    label = { Text("R$ 0,00") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                // Campo de texto para a data (não editável diretamente)
                OutlinedTextField(
                    value = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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
            // Menu dropdown para selecionar a categoria
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.descricao) },
                            onClick = {
                                onCategoriaIdChange(categoria._id)
                                selectedCategoryText = categoria.descricao
                                expanded = false
                            }
                        )
                    }
                }
            }
            // Botão para salvar
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
    // Lista de categorias de exemplo
    val mockCategorias = listOf(
        Categorias(1, "Mercado", TipoCategoria.Perda, 0),
        Categorias(2, "Alimentação", TipoCategoria.Perda, 1),
        Categorias(3, "Lazer", TipoCategoria.Perda, 5)
    )

    ControleFinanceiroTheme {
        // Usa o componente de UI desacoplado com dados de exemplo
        TelaLancamentoContaContent(
            descricao = "Compra de teste",
            onDescricaoChange = {},
            valor = 123.45,
            onValorChange = {},
            data = LocalDate.now(),
            onDataChange = {},
            categoriaId = 1,
            onCategoriaIdChange = {},
            categorias = mockCategorias,
            onSaveClick = {},
            onBackClick = {},
            isEditMode = false
        )
    }
}