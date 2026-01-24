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
import java.math.RoundingMode
import java.time.LocalDate

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHandler = DatabaseHandler.getInstance(application)

    private val _totalGastoMes = MutableStateFlow(BigDecimal.ZERO)
    val totalGastoMes: StateFlow<BigDecimal> = _totalGastoMes

    private val _categoriasMaisGastas = MutableStateFlow<List<CategoriaMaisGasta>>(emptyList())
    val categoriasMaisGastas: StateFlow<List<CategoriaMaisGasta>> = _categoriasMaisGastas

    private val _ultimosLancamentos = MutableStateFlow<List<UltimoLancamento>>(emptyList())
    val ultimosLancamentos: StateFlow<List<UltimoLancamento>> = _ultimosLancamentos

    // StateFlow para a variação percentual em relação ao mês anterior.
    private val _variacaoMes = MutableStateFlow(0.0)
    val variacaoMes: StateFlow<Double> = _variacaoMes

    // StateFlow para o gasto proporcional do mês anterior, usado como base de comparação.
    private val _gastoProporcionalMesAnterior = MutableStateFlow(BigDecimal.ZERO)
    val gastoProporcionalMesAnterior: StateFlow<BigDecimal> = _gastoProporcionalMesAnterior

    // StateFlow para a projeção de gastos para o final do mês atual.
    private val _projecaoGastoMes = MutableStateFlow(BigDecimal.ZERO)
    val projecaoGastoMes: StateFlow<BigDecimal> = _projecaoGastoMes

    // StateFlow para a média diária de gastos do mês atual.
    private val _mediaDiariaAtual = MutableStateFlow(BigDecimal.ZERO)
    val mediaDiariaAtual: StateFlow<BigDecimal> = _mediaDiariaAtual

    fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Carrega os dados existentes do dashboard.
            val totalGastoAtual = dbHandler.getTotalGastoMesAtual()
            _totalGastoMes.value = totalGastoAtual
            _categoriasMaisGastas.value = dbHandler.getCategoriasMaisGastasMesAtual()
            _ultimosLancamentos.value = dbHandler.getUltimosLancamentos()

            // Define as datas para os cálculos.
            val hoje = LocalDate.now()
            val mesAnterior = hoje.minusMonths(1)
            val diasNoMesAnterior = mesAnterior.lengthOfMonth()
            val diaAtual = hoje.dayOfMonth
            val diasNoMesAtual = hoje.lengthOfMonth()

            // Calcula o comparativo com o mês anterior.
            val totalGastoMesAnterior = dbHandler.getTotalGastoMesAnterior()
            if (diasNoMesAnterior > 0 && totalGastoMesAnterior > BigDecimal.ZERO) {
                val mediaDiariaMesAnterior = totalGastoMesAnterior.divide(BigDecimal(diasNoMesAnterior), 2, RoundingMode.HALF_UP)
                val gastoProporcional = mediaDiariaMesAnterior.multiply(BigDecimal(diaAtual))
                _gastoProporcionalMesAnterior.value = gastoProporcional

                if (gastoProporcional > BigDecimal.ZERO) {
                    val variacao = (totalGastoAtual.subtract(gastoProporcional))
                        .divide(gastoProporcional, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                    _variacaoMes.value = variacao.toDouble()
                } else {
                    _variacaoMes.value = if (totalGastoAtual > BigDecimal.ZERO) 100.0 else 0.0
                }
            } else {
                _gastoProporcionalMesAnterior.value = BigDecimal.ZERO
                _variacaoMes.value = if (totalGastoAtual > BigDecimal.ZERO) 100.0 else 0.0
            }

            // Calcula a projeção de gastos para o final do mês atual.
            if (diaAtual > 0 && totalGastoAtual > BigDecimal.ZERO) {
                val mediaDiariaAtual = totalGastoAtual.divide(BigDecimal(diaAtual), 2, RoundingMode.HALF_UP)
                _mediaDiariaAtual.value = mediaDiariaAtual
                val projecao = mediaDiariaAtual.multiply(BigDecimal(diasNoMesAtual))
                _projecaoGastoMes.value = projecao
            } else {
                _projecaoGastoMes.value = BigDecimal.ZERO
                _mediaDiariaAtual.value = BigDecimal.ZERO
            }
        }
    }

    fun deleteLancamento(lancamento: UltimoLancamento) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.deleteLancamentoById(lancamento._id)
            loadDashboardData()
        }
    }
}
