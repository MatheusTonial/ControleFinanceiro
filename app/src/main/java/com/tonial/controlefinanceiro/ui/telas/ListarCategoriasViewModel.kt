// ViewModel para a tela de listagem de categorias, responsável por carregar e excluir categorias.
package com.tonial.controlefinanceiro.ui.telas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListarCategoriasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseHandler.getInstance(application)

    private val _categorias = MutableStateFlow<List<Categorias>>(emptyList())
    val categorias: StateFlow<List<Categorias>> = _categorias

    // Carrega todas as categorias do banco de dados.
    fun loadCategorias() {
        viewModelScope.launch(Dispatchers.IO) {
            _categorias.value = db.getAllCategorias()
        }
    }

    // Exclui uma categoria e atualiza a lista.
    fun deleteCategoria(categoria: Categorias) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteCategoriaById(categoria._id)
            loadCategorias()
        }
    }
}
