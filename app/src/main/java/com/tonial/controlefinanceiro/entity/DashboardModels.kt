package com.tonial.controlefinanceiro.entity

import java.math.BigDecimal

data class CategoriaMaisGasta(
    val categoria: String,
    val total: BigDecimal
)

data class UltimoLancamento(
    val descricao: String,
    val categoria: String,
    val valor: BigDecimal,
    val data: String,
    val tipo: String
)
