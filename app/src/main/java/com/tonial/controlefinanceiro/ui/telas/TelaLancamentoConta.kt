package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLancamentoConta(
    modifier: Modifier = Modifier,
    viewModel: FluxoViewModel,
    categorias: List<Categorias>,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    lancamentoId: Long? = null,
    gastoRecorrenteId: Long? = null,
    isOpenedFromWidget: Boolean = false
) {
    LaunchedEffect(lancamentoId, gastoRecorrenteId) {
        when {
            lancamentoId != null -> viewModel.loadConta(lancamentoId)
            gastoRecorrenteId != null -> viewModel.loadGastoRecorrente(gastoRecorrenteId)
            else -> viewModel.limpaConta()
        }
    }

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
        lancamentoUnico = viewModel.lancamentoUnico_conta,
        onLancamentoUnicoChange = { viewModel.onLancamentoUnicoContaChange(it) },
        onSaveClick = onSaveClick,
        onBackClick = onBackClick,
        isEditMode = lancamentoId != null,
        isFromGastoRecorrente = gastoRecorrenteId != null
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
    lancamentoUnico: Boolean,
    onLancamentoUnicoChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    isEditMode: Boolean,
    isFromGastoRecorrente: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (!isEditMode) "Novo Lançamento" else "Editar Lançamento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        val descricaoFocusRequester = remember { FocusRequester() }
        val valorFocusRequester = remember { FocusRequester() }
        var showDatePicker by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }

        // Definimos cores escuras explícitas para evitar o branco do tema sobre o fundo creme
        val darkText = MaterialTheme.colorScheme.onSurface
        val darkLabel = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = darkText,
            unfocusedTextColor = darkText,
            disabledTextColor = darkText,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = darkLabel,
            disabledLabelColor = darkLabel,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedTrailingIconColor = darkLabel,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = darkLabel,
            unfocusedPlaceholderColor = darkText.copy(alpha = 0.5f)
        )

        LaunchedEffect(Unit) {
            if (!isEditMode && !isFromGastoRecorrente) {
                descricaoFocusRequester.requestFocus()
            }
        }

        var selectedCategoryText by remember(categoriaId, categorias) {
            mutableStateOf(categorias.find { it._id == categoriaId }?.descricao ?: "")
        }

        var valorDigits by remember(valor) {
            val valorEmCentavos = BigDecimal.valueOf(valor).multiply(BigDecimal(100)).toLong()
            mutableStateOf(if (valorEmCentavos == 0L) "" else valorEmCentavos.toString())
        }

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
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descricaoFocusRequester),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { valorFocusRequester.requestFocus() }
                ),
                colors = textFieldColors
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val focusManager = LocalFocusManager.current
                OutlinedTextField(
                    value = valorDigits,
                    onValueChange = { newDigits ->
                        val filteredDigits = newDigits.filter { it.isDigit() }
                        if (filteredDigits.length <= 15) {
                            valorDigits = filteredDigits
                            val newValue = filteredDigits.toLongOrNull()?.div(100.0) ?: 0.0
                            onValorChange(newValue)
                        }
                    },
                    label = { Text("Valor") },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(valorFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                            expanded = true
                        }
                    ),
                    visualTransformation = CurrencyVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = { },
                    enabled = false,
                    label = { Text("Data") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    colors = textFieldColors
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.descricao, color = darkText) },
                            onClick = {
                                onCategoriaIdChange(categoria._id)
                                selectedCategoryText = categoria.descricao
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isFromGastoRecorrente) {
                    Text("Este é um lançamento de uma conta recorrente.", color = darkText)
                } else {
                    Text("Lançamento único", color = darkText)
                    Switch(
                        checked = lancamentoUnico,
                        onCheckedChange = onLancamentoUnicoChange
                    )
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

private class CurrencyVisualTransformation : VisualTransformation {
    private val currencyFormat = DecimalFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val value = digits.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal.ZERO
        val formattedText = currencyFormat.format(value)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return digits.length
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

@Preview(showBackground = true)
@Composable
fun TelaLancamentoContaPreview() {
    val mockCategorias = listOf(
        Categorias(1, "Mercado", TipoCategoria.Perda, 0),
        Categorias(2, "Alimentação", TipoCategoria.Perda, 1),
        Categorias(3, "Lazer", TipoCategoria.Perda, 5)
    )

    ControleFinanceiroTheme {
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
            lancamentoUnico = false,
            onLancamentoUnicoChange = {},
            onSaveClick = {},
            onBackClick = {},
            isEditMode = false,
            isFromGastoRecorrente = false
        )
    }
}
