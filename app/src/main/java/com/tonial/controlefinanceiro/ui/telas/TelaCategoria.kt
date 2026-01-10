// Tela para cadastrar ou editar uma categoria.
package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.model.FluxoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCategoria(
    viewModel: FluxoViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    categoriaId: Long? = null
) {
    // Carrega a categoria para edição ou limpa o formulário para um novo cadastro.
    LaunchedEffect(categoriaId) {
        if (categoriaId != null) {
            viewModel.loadCategoria(categoriaId)
        } else {
            viewModel.limpaCategoria()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (categoriaId == null) "Nova Categoria" else "Editar Categoria") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = viewModel.descricao_categoria,
                onValueChange = { viewModel.onDescricaoCategoriaChange(it) },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.tipo_categoria == TipoCategoria.Ganho,
                        onClick = { viewModel.onTipoCategoriaChange(TipoCategoria.Ganho) }
                    )
                    Text("Ganho")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.tipo_categoria == TipoCategoria.Perda,
                        onClick = { viewModel.onTipoCategoriaChange(TipoCategoria.Perda) }
                    )
                    Text("Perda")
                }
            }
            OutlinedTextField(
                value = viewModel.ordem_categoria.toString(),
                onValueChange = { viewModel.onOrdemCategoriaChange(it.toIntOrNull() ?: 0) },
                label = { Text("Ordem") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Salvar")
            }
        }
    }
}
