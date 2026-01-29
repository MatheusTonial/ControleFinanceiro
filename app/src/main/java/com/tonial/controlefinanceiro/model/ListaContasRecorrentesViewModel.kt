package com.tonial.controlefinanceiro.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Contas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListaContasRecorrentesViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHandler = DatabaseHandler.getInstance(application)

    private val _contasRecorrentes = MutableStateFlow<List<Contas>>(emptyList())
    val contasRecorrentes: StateFlow<List<Contas>> = _contasRecorrentes

    fun loadContasRecorrentes() {
        viewModelScope.launch(Dispatchers.IO) {
            _contasRecorrentes.value = dbHandler.getAllGastosRecorrentes()
        }
    }

    fun deleteContaRecorrente(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.deleteGastoRecorrenteById(id)
            loadContasRecorrentes()
        }
    }
}