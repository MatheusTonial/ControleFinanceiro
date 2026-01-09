package com.tonial.controlefinanceiro.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHandler = DatabaseHandler.getInstance(application)

    private val _totalGastoMes = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val totalGastoMes: StateFlow<BigDecimal> = _totalGastoMes

    private val _categoriasMaisGastas = MutableStateFlow<List<CategoriaMaisGasta>>(emptyList())
    val categoriasMaisGastas: StateFlow<List<CategoriaMaisGasta>> = _categoriasMaisGastas

    private val _ultimosLancamentos = MutableStateFlow<List<UltimoLancamento>>(emptyList())
    val ultimosLancamentos: StateFlow<List<UltimoLancamento>> = _ultimosLancamentos

    fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            _totalGastoMes.value = dbHandler.getTotalGastoMesAtual()
            _categoriasMaisGastas.value = dbHandler.getCategoriasMaisGastasMesAtual()
            _ultimosLancamentos.value = dbHandler.getUltimosLancamentos()
        }
    }
}
