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
    gastoRecorrenteId: Long? = null, // Parâmetro para o ID do gasto recorrente.
    isOpenedFromWidget: Boolean = false
) {
    // Efeito para carregar os dados da conta, seja um lançamento existente, um gasto recorrente ou um novo lançamento.
    LaunchedEffect(lancamentoId, gastoRecorrenteId) {
        when {
            lancamentoId != null -> viewModel.loadConta(lancamentoId)
            gastoRecorrenteId != null -> viewModel.loadGastoRecorrente(gastoRecorrenteId)
            else -> viewModel.limpaConta()
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
                }
            )
        }
    ) { paddingValues ->
        val descricaoFocusRequester = remember { FocusRequester() }
        val valorFocusRequester = remember { FocusRequester() }
        var showDatePicker by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }

        // Efeito para solicitar foco no campo de descrição ao entrar na tela, agilizando o lançamento
        LaunchedEffect(Unit) {
            if (!isEditMode && !isFromGastoRecorrente) {
                descricaoFocusRequester.requestFocus()
            }
        }

        // Texto da categoria selecionada
        var selectedCategoryText by remember(categoriaId, categorias) {
            mutableStateOf(categorias.find { it._id == categoriaId }?.descricao ?: "")
        }

        // Armazena o valor monetário como uma string de dígitos (ex: "12345" para 123,45)
        var valorDigits by remember(valor) {
            mutableStateOf(if (valor == 0.0) "" else (valor * 100).toLong().toString())
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
                    .focusRequester(descricaoFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { valorFocusRequester.requestFocus() }
                ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val focusManager = LocalFocusManager.current
                // Campo de texto para o valor com formatação de moeda
                OutlinedTextField(
                    value = valorDigits,
                    onValueChange = { newDigits ->
                        // Permite apenas a entrada de dígitos
                        val filteredDigits = newDigits.filter { it.isDigit() }
                        // Limita o comprimento para evitar overflow
                        if (filteredDigits.length <= 15) {
                            valorDigits = filteredDigits
                            // Converte a string de dígitos para Double (ex: "123" -> 1.23)
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
                    // Aplica a transformação visual para formatar como moeda
                    visualTransformation = CurrencyVisualTransformation(),
                    // Alinha o texto à direita
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isFromGastoRecorrente) {
                    Text("Este é um lançamento de uma conta recorrente.")
                } else {
                    Text("Lançamento único")
                    Switch(
                        checked = lancamentoUnico,
                        onCheckedChange = onLancamentoUnicoChange
                    )
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

// Classe para a transformação visual da moeda
private class CurrencyVisualTransformation : VisualTransformation {
    // Formato de moeda para o Brasil (R$)
    private val currencyFormat = DecimalFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun filter(text: AnnotatedString): TransformedText {
        // Pega a string de dígitos crus (ex: "12345")
        val digits = text.text

        // Converte para BigDecimal (ex: 123.45)
        val value = digits.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal.ZERO

        // Formata o valor como moeda (ex: "R$ 123,45")
        val formattedText = currencyFormat.format(value)

        // Mapeamento de offset para manter o cursor sempre no final.
        // Essencial para a entrada de valores da direita para a esquerda.
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
            lancamentoUnico = false,
            onLancamentoUnicoChange = {},
            onSaveClick = {},
            onBackClick = {},
            isEditMode = false,
            isFromGastoRecorrente = false
        )
    }
}
