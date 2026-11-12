package com.tonial.controlefinanceiro.ui.analise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.GastoMensal
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.ui.dashboard.calcularProjecaoMes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class AnaliseViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val MESES_EVOLUCAO = 12
    }

    private val dbHandler = DatabaseHandler.getInstance(application)

    // Categorias de gasto disponíveis no filtro e a selecionada (null = todas).
    private val _categorias = MutableStateFlow<List<Categorias>>(emptyList())
    val categorias: StateFlow<List<Categorias>> = _categorias

    private val _categoriaFiltro = MutableStateFlow<Long?>(null)
    val categoriaFiltro: StateFlow<Long?> = _categoriaFiltro

    private var jobFiltrados: Job? = null
    private var jobCategoria: Job? = null

    // Evolução mensal: total gasto em cada um dos últimos meses.
    private val _gastosMensais = MutableStateFlow<List<GastoMensal>>(emptyList())
    val gastosMensais: StateFlow<List<GastoMensal>> = _gastosMensais

    // Gasto por categoria no mês selecionado.
    private val _mesCategoria = MutableStateFlow(YearMonth.now())
    val mesCategoria: StateFlow<YearMonth> = _mesCategoria

    private val _gastosPorCategoria = MutableStateFlow<List<CategoriaMaisGasta>>(emptyList())
    val gastosPorCategoria: StateFlow<List<CategoriaMaisGasta>> = _gastosPorCategoria

    // Gasto acumulado dia a dia (índice 0 = dia 1).
    private val _acumuladoAtual = MutableStateFlow<List<BigDecimal>>(emptyList())
    val acumuladoAtual: StateFlow<List<BigDecimal>> = _acumuladoAtual

    private val _acumuladoAnterior = MutableStateFlow<List<BigDecimal>>(emptyList())
    val acumuladoAnterior: StateFlow<List<BigDecimal>> = _acumuladoAnterior

    private val _acumuladoRetrasado = MutableStateFlow<List<BigDecimal>>(emptyList())
    val acumuladoRetrasado: StateFlow<List<BigDecimal>> = _acumuladoRetrasado

    // Projeção de gastos para o final do mês atual (mesma regra do Dashboard).
    private val _projecaoMes = MutableStateFlow(BigDecimal.ZERO)
    val projecaoMes: StateFlow<BigDecimal> = _projecaoMes

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _categorias.value = dbHandler.getAllCategorias().filter { it.tipo == TipoCategoria.Perda }
        }
        selecionarMesCategoria(_mesCategoria.value)
        carregarFiltrados()
    }

    fun selecionarCategoriaFiltro(categoriaId: Long?) {
        if (_categoriaFiltro.value == categoriaId) return
        _categoriaFiltro.value = categoriaId
        carregarFiltrados()
    }

    // Carrega a evolução mensal e o acumulado do mês, que respeitam o filtro de categoria.
    private fun carregarFiltrados() {
        val categoriaId = _categoriaFiltro.value
        jobFiltrados?.cancel()
        jobFiltrados = viewModelScope.launch(Dispatchers.IO) {
            _gastosMensais.value = dbHandler.getTotaisPorMes(MESES_EVOLUCAO, categoriaId)

            val hoje = LocalDate.now()
            val mesAtual = YearMonth.from(hoje)
            _acumuladoAtual.value = acumular(dbHandler.getGastosDiarios(mesAtual, categoriaId), hoje.dayOfMonth)
            val mesAnterior = mesAtual.minusMonths(1)
            _acumuladoAnterior.value = acumular(dbHandler.getGastosDiarios(mesAnterior, categoriaId), mesAnterior.lengthOfMonth())
            val mesRetrasado = mesAtual.minusMonths(2)
            _acumuladoRetrasado.value = acumular(dbHandler.getGastosDiarios(mesRetrasado, categoriaId), mesRetrasado.lengthOfMonth())

            val totalGasto = dbHandler.getTotalGastoMesAtual(categoriaId)
            val gastosNormais = totalGasto
                .subtract(dbHandler.getTotalGastoUnicosMesAtual(categoriaId))
                .subtract(dbHandler.getTotalGastoRecorrenteMesAtual(categoriaId))
            _projecaoMes.value = calcularProjecaoMes(totalGasto, gastosNormais, dbHandler.getTotalTabelaRecorrenteMesAtual(categoriaId), hoje).projecao
        }
    }

    fun mesCategoriaAnterior() = selecionarMesCategoria(_mesCategoria.value.minusMonths(1))

    fun mesCategoriaProximo() {
        // Não avança além do mês atual.
        if (_mesCategoria.value < YearMonth.now()) {
            selecionarMesCategoria(_mesCategoria.value.plusMonths(1))
        }
    }

    private fun selecionarMesCategoria(mes: YearMonth) {
        _mesCategoria.value = mes
        jobCategoria?.cancel()
        jobCategoria = viewModelScope.launch(Dispatchers.IO) {
            _gastosPorCategoria.value = dbHandler.getGastosPorCategoria(mes)
        }
    }

    private fun acumular(gastosDiarios: Map<Int, BigDecimal>, ateDia: Int): List<BigDecimal> {
        var soma = BigDecimal.ZERO
        return (1..ateDia).map { dia ->
            soma += gastosDiarios[dia] ?: BigDecimal.ZERO
            soma
        }
    }
}
