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
    // É exibido quando showDeleteDialog é true.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Categoria") },
            text = { Text("Tem certeza que deseja excluir esta categoria?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Ao confirmar, chama a função de exclusão no ViewModel.
                        categoriaToDelete?.let { viewModel.deleteCategoria(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                // Fecha o diálogo ao cancelar.
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Estrutura principal da tela com TopAppBar e FloatingActionButton.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorias") },
                navigationIcon = {
                    // Botão para voltar à tela anterior.
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão para adicionar uma nova categoria.
            FloatingActionButton(
                onClick = { onNavigateToCategoria(null) }, // Passa null para indicar criação.
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Categoria")
            }
        }
    ) { paddingValues ->
        // Lista de categorias exibida em uma LazyColumn para otimização de performance.
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(categorias) { categoria ->
                // Componente para exibir cada item da lista de categorias.
                CategoriaRow(
                    categoria = categoria,
                    onEdit = { onNavigateToCategoria(it._id) }, // Navega para edição com o ID da categoria.
                    onDelete = {
                        // Define a categoria a ser excluída e exibe o diálogo de confirmação.
                        categoriaToDelete = it
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

// Composable que representa uma linha na lista de categorias.
@Composable
fun CategoriaRow(
    categoria: Categorias,
    onEdit: (Categorias) -> Unit,
    onDelete: (Categorias) -> Unit
) {
    // Estado para controlar a exibição do menu de opções (editar/excluir).
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exibe la descripción y el tipo de categoría.
            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.descricao, fontWeight = FontWeight.SemiBold)
                Text(categoria.tipo.name, style = MaterialTheme.typography.bodySmall)
            }
            // Botão de menu com opções de editar e excluir.
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
