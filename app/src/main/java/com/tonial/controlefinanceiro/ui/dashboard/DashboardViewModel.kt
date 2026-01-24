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


    fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Carrega os dados existentes do dashboard.
            val totalGastoAtual = dbHandler.getTotalGastoMesAtual()
            _totalGastoMes.value = totalGastoAtual
            _categoriasMaisGastas.value = dbHandler.getCategoriasMaisGastasMesAtual()
            _ultimosLancamentos.value = dbHandler.getUltimosLancamentos()

            // Calcula o comparativo com o mês anterior.
            // Esta lógica assume a existência de um método `getTotalGastoMesAnterior()` no `DatabaseHandler`.
            val hoje = LocalDate.now()
            val mesAnterior = hoje.minusMonths(1)

            val totalGastoMesAnterior = dbHandler.getTotalGastoMesAnterior()
            val diasNoMesAnterior = mesAnterior.lengthOfMonth()
            val diaAtual = hoje.dayOfMonth

            if (diasNoMesAnterior > 0 && totalGastoMesAnterior > BigDecimal.ZERO) {
                // Calcula a média de gasto diário do mês anterior.
                val mediaDiariaMesAnterior = totalGastoMesAnterior.divide(BigDecimal(diasNoMesAnterior), 2, RoundingMode.HALF_UP)
                // Calcula o gasto proporcional esperado para o período atual do mês.
                val gastoProporcional = mediaDiariaMesAnterior.multiply(BigDecimal(diaAtual))
                _gastoProporcionalMesAnterior.value = gastoProporcional

                if (gastoProporcional > BigDecimal.ZERO) {
                    // Calcula a variação percentual entre o gasto atual e o gasto proporcional.
                    val variacao = (totalGastoAtual.subtract(gastoProporcional))
                        .divide(gastoProporcional, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                    _variacaoMes.value = variacao.toDouble()
                } else {
                    // Se o gasto proporcional for zero, qualquer gasto atual é um aumento de 100%.
                    _variacaoMes.value = if (totalGastoAtual > BigDecimal.ZERO) 100.0 else 0.0
                }
            } else {
                // Se não houve gastos no mês anterior, não é possível comparar.
                _gastoProporcionalMesAnterior.value = BigDecimal.ZERO
                // Se não houve gasto proporcional, qualquer gasto atual é um aumento de 100%.
                _variacaoMes.value = if (totalGastoAtual > BigDecimal.ZERO) 100.0 else 0.0
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
