// Tela para listar, editar e excluir categorias.
package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.entity.Categorias

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarCategoriasScreen(
    onBackClick: () -> Unit,
    onNavigateToCategoria: (Long?) -> Unit,
    viewModel: ListarCategoriasViewModel = viewModel()
) {
    // Estados para controlar a lista de categorias e o diálogo de exclusão.
    val categorias by viewModel.categorias.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoriaToDelete by remember { mutableStateOf<Categorias?>(null) }

    // Carrega as categorias do ViewModel sempre que a tela for iniciada.
    LaunchedEffect(Unit) {
        viewModel.loadCategorias()
    }

    // Diálogo de confirmação para excluir uma categoria.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Categoria") },
            text = { Text("Tem certeza que deseja excluir esta categoria?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoriaToDelete?.let { viewModel.deleteCategoria(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorias") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                // Define a cor TerraformPrimary para o TopAppBar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCategoria(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Categoria")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(categorias) { categoria ->
                CategoriaRow(
                    categoria = categoria,
                    onEdit = { onNavigateToCategoria(it._id) },
                    onDelete = {
                        categoriaToDelete = it
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun CategoriaRow(
    categoria: Categorias,
    onEdit: (Categorias) -> Unit,
    onDelete: (Categorias) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.descricao, fontWeight = FontWeight.SemiBold)
                Text(categoria.tipo.name, style = MaterialTheme.typography.bodySmall)
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { onEdit(categoria); expanded = false })
                    DropdownMenuItem(text = { Text("Excluir") }, onClick = { onDelete(categoria); expanded = false })
                }
            }
        }
    }
}
