package com.tonial.controlefinanceiro.ui.historico

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoricoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseHandler.getInstance(application)

    private val _historico = MutableStateFlow<List<UltimoLancamento>>(emptyList())
    val historico: StateFlow<List<UltimoLancamento>> = _historico

    private val _categorias = MutableStateFlow<List<Categorias>>(emptyList())
    val categorias: StateFlow<List<Categorias>> = _categorias

    private var currentDataInicio: LocalDate = LocalDate.now().withDayOfMonth(1)
    private var currentDataFim: LocalDate = LocalDate.now()
    private var currentCategoriaId: Long? = null

    fun loadHistorico(dataInicio: LocalDate, dataFim: LocalDate, categoriaId: Long?) {
        currentDataInicio = dataInicio
        currentDataFim = dataFim
        currentCategoriaId = categoriaId
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            _historico.value = db.getHistorico(dataInicio.format(formatter), dataFim.format(formatter), categoriaId)
        }
    }

    fun loadCategorias() {
        viewModelScope.launch {
            _categorias.value = db.getAllCategorias()
        }
    }

    fun deleteLancamento(lancamento: UltimoLancamento) {
        viewModelScope.launch {
            db.deleteLancamentoById(lancamento._id)
            loadHistorico(currentDataInicio, currentDataFim, currentCategoriaId)
        }
    }
}