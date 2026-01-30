package com.tonial.controlefinanceiro.ui.telas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.model.ListaContasRecorrentesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarContasRecorrentesScreen(
    onBackClick: () -> Unit,
    onNavigateToContaRecorrente: (Long?) -> Unit,
    onLaunchConta: (Long) -> Unit,
    viewModel: ListaContasRecorrentesViewModel = viewModel()
) {
    val contasRecorrentes by viewModel.contasRecorrentes.collectAsState()

    // Carrega a lista de contas recorrentes ao entrar na tela.
    LaunchedEffect(Unit) {
        viewModel.loadContasRecorrentes()
    }

    // Busca as categorias para exibir o nome em vez do ID.
    val context = LocalContext.current
    val banco = remember { DatabaseHandler.getInstance(context) }
    var categorias by remember { mutableStateOf<List<Categorias>>(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            categorias = banco.getAllCategorias()
        }
    }
    // Cria um mapa de categorias para facilitar a busca do nome.
    val categoryMap = categorias.associate { it._id to it.descricao }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contas Recorrentes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(contasRecorrentes) { conta ->
                // O estado de 'expanded' agora é local para cada item do card.
                var expanded by remember { mutableStateOf(false) }

                // Lógica para determinar a cor da borda com base na data.
                val hoje = LocalDate.now()
                val dataConta = conta.data
                val borderColor = when {
                    // Meses futuros: borda verde.
                    dataConta.year > hoje.year || (dataConta.year == hoje.year && dataConta.monthValue > hoje.monthValue) -> Color(
                        0xFF7AB77E
                    ) // Verde
                    // Mês atual: borda amarela.
                    dataConta.year == hoje.year && dataConta.monthValue == hoje.monthValue -> Color(
                        0xFFF1E372
                    ) // Amarelo
                    // Meses passados: borda vermelha.
                    else -> Color(0xFFC03A2E) // Vermelho
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    // Adiciona a borda colorida ao card.
                    border = BorderStroke(2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Coluna para descrição, data e categoria.
                        Column(modifier = Modifier.weight(1f)) {
                            Text(conta.descricao, fontWeight = FontWeight.SemiBold)
                            // Altera o formato da data para dd/MM/yyyy.
                            val dataFormatada =
                                conta.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            // Exibe o nome da categoria.
                            val categoriaNome = categoryMap[conta.categoriaId] ?: "Sem categoria"
                            Text("$dataFormatada • $categoriaNome", fontSize = 12.sp)
                        }

                        val cor = MaterialTheme.colorScheme.error
                        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        val valorFormatado = numberFormat.format(conta.valor)

                        // Linha para o valor e o menu de opções.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = valorFormatado,
                                color = cor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Box {
                                // Botão para abrir o menu de opções.
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Mais opções"
                                    )
                                }
                                // Menu de opções com os itens de editar e excluir.
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Lançar conta") },
                                        leadingIcon = { Icon(Icons.Default.ArrowForward, contentDescription = null) },
                                        onClick = {
                                            onLaunchConta(conta._id)
                                            expanded = false
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Editar") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                        onClick = {
                                            onNavigateToContaRecorrente(conta._id)
                                            expanded = false
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Excluir") },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                        onClick = {
                                            viewModel.deleteContaRecorrente(conta._id)
                                            expanded = false
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}