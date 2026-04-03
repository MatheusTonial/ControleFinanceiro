package com.tonial.controlefinanceiro.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import com.tonial.controlefinanceiro.widget.WidgetUpdateWorker
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

    // StateFlow para a contagem de lançamentos no mês atual.
    private val _lancamentosCount = MutableStateFlow(0)

    //valor de gastos do mes
    private val _gastosNormaisMes = MutableStateFlow(BigDecimal.ZERO)
    private val _gastosUnicosMes = MutableStateFlow(BigDecimal.ZERO)
    private val _gastosRecorrentesMes = MutableStateFlow(BigDecimal.ZERO)

    private val _gastosTabelaRecorrentesMes = MutableStateFlow(BigDecimal.ZERO)

    val lancamentosCount: StateFlow<Int> = _lancamentosCount

    fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Carrega os dados existentes do dashboard.
            //val totalGastoAtual = dbHandler.getTotalGastoMesAtual()
            _totalGastoMes.value = dbHandler.getTotalGastoMesAtual()//totalGastoAtual
            _categoriasMaisGastas.value = dbHandler.getCategoriasMaisGastasMesAtual()
            _ultimosLancamentos.value = dbHandler.getUltimosLancamentos()
            _lancamentosCount.value = dbHandler.getLancamentosCountMesAtual()
            _gastosUnicosMes.value = dbHandler.getTotalGastoUnicosMesAtual()
            _gastosRecorrentesMes.value = dbHandler.getTotalGastoRecorrenteMesAtual()
            _gastosNormaisMes.value = _totalGastoMes.value.subtract(_gastosUnicosMes.value).subtract(_gastosRecorrentesMes.value)

            _gastosTabelaRecorrentesMes.value = dbHandler.getTotalTabelaRecorrenteMesAtual()

            // Define as datas para os cálculos.
            val hoje = LocalDate.now()
            val mesAnterior = hoje.minusMonths(1)
            val diasNoMesAnterior = mesAnterior.lengthOfMonth()
            val diaAtual = hoje.dayOfMonth
            // Obtém o número total de dias no mês atual.
            val diasNoMesAtual = hoje.lengthOfMonth()
            // Calcula quantos dias faltam para o final do mês.
            val diasRestantes = diasNoMesAtual - diaAtual

            // Calcula o comparativo com o mês anterior, ignora os gastos unicos de ambos os meses
            val totalGastoMesAnterior = dbHandler.getTotalGastoMesAnterior()
            if (diasNoMesAnterior > 0 && totalGastoMesAnterior > BigDecimal.ZERO) {
                val mediaDiariaMesAnterior = totalGastoMesAnterior.divide(BigDecimal(diasNoMesAnterior), 2, RoundingMode.HALF_UP)
                val gastoProporcional = mediaDiariaMesAnterior.multiply(BigDecimal(diaAtual))
                _gastoProporcionalMesAnterior.value = gastoProporcional

                if (gastoProporcional > BigDecimal.ZERO) {
                    val variacao = (_totalGastoMes.value .subtract(_gastosUnicosMes.value).subtract(gastoProporcional))
                        .divide(gastoProporcional, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                    _variacaoMes.value = variacao.toDouble()
                } else {
                    _variacaoMes.value = if (_totalGastoMes.value  > BigDecimal.ZERO) 100.0 else 0.0
                }
            }
            else {
                _gastoProporcionalMesAnterior.value = BigDecimal.ZERO
                _variacaoMes.value = if (_totalGastoMes.value  > BigDecimal.ZERO) 100.0 else 0.0
            }

            // Calcula a projeção de gastos para o final do mês atual.
            if (diaAtual > 0 && _totalGastoMes.value  > BigDecimal.ZERO) {
                val mediaDiariaAtual = _gastosNormaisMes.value.divide(BigDecimal(diaAtual), 2, RoundingMode.HALF_UP)
                _mediaDiariaAtual.value = mediaDiariaAtual
                val mediaRestoMes = mediaDiariaAtual.multiply(BigDecimal(diasRestantes))
                val projecao = _totalGastoMes.value  + mediaRestoMes + _gastosTabelaRecorrentesMes.value
                _projecaoGastoMes.value = projecao
            } else {
                _projecaoGastoMes.value = BigDecimal.ZERO
                _mediaDiariaAtual.value = BigDecimal.ZERO
            }
            /*
                projecao =
                ( média_diária_gastos_normais × dias_restantes )
                + recorrentes_restantes_do_mês
                + gastos_ja_feitos
            */
        }
    }

    fun deleteLancamento(lancamento: UltimoLancamento) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.deleteLancamentoById(lancamento._id)
            loadDashboardData()
            // Atualiza o widget após deletar
            WidgetUpdateWorker.enqueue(getApplication())
        }
    }
}
