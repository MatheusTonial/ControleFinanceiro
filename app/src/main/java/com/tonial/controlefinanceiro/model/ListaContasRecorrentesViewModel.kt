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
import kotlinx.coroutines.withContext

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

    // Atualiza a descrição, o valor e a data de uma conta recorrente para o mês seguinte.
    fun updateRecurringAccount(id: Long, newDescription: String, newValue: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val conta = dbHandler.getGastoRecorrenteById(id)
            if (conta != null) {
                val novaData = conta.data.plusMonths(1)
                val contaAtualizada = conta.copy(descricao = newDescription, data = novaData, valor = newValue)
                dbHandler.updateGastoRecorrente(contaAtualizada)
                // Recarrega a lista para refletir a alteração na UI.
                withContext(Dispatchers.Main) {
                    loadContasRecorrentes()
                }
            }
        }
    }
}