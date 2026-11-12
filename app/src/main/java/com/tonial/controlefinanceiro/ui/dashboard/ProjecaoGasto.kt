package com.tonial.controlefinanceiro.ui.dashboard

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class ProjecaoMes(
    val mediaDiaria: BigDecimal,
    val projecao: BigDecimal
)

/*
    projecao =
    ( média_diária_gastos_normais × dias_restantes )
    + recorrentes_restantes_do_mês
    + gastos_ja_feitos
*/
fun calcularProjecaoMes(
    totalGasto: BigDecimal,
    gastosNormais: BigDecimal,
    totalTabelaRecorrente: BigDecimal,
    hoje: LocalDate = LocalDate.now()
): ProjecaoMes {
    val diaAtual = hoje.dayOfMonth
    if (diaAtual <= 0 || totalGasto <= BigDecimal.ZERO) {
        return ProjecaoMes(BigDecimal.ZERO, BigDecimal.ZERO)
    }
    val diasRestantes = hoje.lengthOfMonth() - diaAtual
    val mediaDiaria = gastosNormais.divide(BigDecimal(diaAtual), 2, RoundingMode.HALF_UP)
    val projecao = totalGasto + mediaDiaria.multiply(BigDecimal(diasRestantes)) + totalTabelaRecorrente
    return ProjecaoMes(mediaDiaria, projecao)
}
