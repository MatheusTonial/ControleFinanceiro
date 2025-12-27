package com.tonial.controlefinanceiro.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.model.FluxoViewModel
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCategoria(
    modifier: Modifier = Modifier,
    viewModel: FluxoViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var ordemText by remember(viewModel.ordem_categoria) {
        // O valor padrão 99 do ViewModel será exibido como um campo vazio.
        mutableStateOf(if (viewModel.ordem_categoria == 99) "" else viewModel.ordem_categoria.toString())
    }

    Column(modifier = modifier.padding(8.dp)) {
        OutlinedTextField(
            value = viewModel.descricao_categoria,
            onValueChange = { viewModel.onDescricaoCategoriaChange(it) },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ordemText,
            onValueChange = { newText ->
                // Permite apenas dígitos
                if (newText.all { it.isDigit() }) {
                    ordemText = newText
                    // Atualiza o ViewModel, com 99 como padrão se o campo estiver vazio
                    viewModel.onOrdemCategoriaChange(newText.toIntOrNull() ?: 99)
                }
            },
            label = { Text("Ordem") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Row(Modifier.selectableGroup().fillMaxWidth().padding(top = 8.dp)) {
            Text("Tipo", modifier = Modifier.align(Alignment.CenterVertically).padding(end = 16.dp))
            TipoCategoria.values().forEach { tipo ->
                Row(
                    Modifier
                        .height(56.dp)
                        .selectable(
                            selected = (tipo == viewModel.tipo_categoria),
                            onClick = { viewModel.onTipoCategoriaChange(tipo) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (tipo == viewModel.tipo_categoria),
                        onClick = null // Ação de clique é controlada pelo Row
                    )
                    Text(
                        text = tipo.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
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

@Preview(showBackground = true)
@Composable
fun TelaCategoriaPreview() {
    ControleFinanceiroTheme {
        val context = LocalContext.current
        TelaCategoria(onSaveClick = { Toast.makeText(context, "Salvo!", Toast.LENGTH_SHORT).show() })
    }
}