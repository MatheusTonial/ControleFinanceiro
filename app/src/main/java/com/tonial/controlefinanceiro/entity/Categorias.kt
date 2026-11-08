package com.tonial.controlefinanceiro.entity

import java.math.BigDecimal
import java.time.YearMonth

data class Categorias (
    val _id: Long,
    val descricao: String,
    val tipo: TipoCategoria,
    val ordem: Int,
    val limiteLancamentos: Int? = null
)

// Dados exibidos no aviso de que a categoria atingiu o limite de lançamentos no mês.
data class AvisoLimiteCategoria (
    val categoria: String,
    val limite: Int,
    val quantidade: Int,
    val total: BigDecimal,
    val mes: YearMonth
)


