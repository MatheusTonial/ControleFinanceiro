package com.tonial.controlefinanceiro.entity

import java.time.LocalDate

data class Contas (
    val _id: Long,
    val descricao: String,
    val valor: Double,
    val data: LocalDate,
    val idRecorrente: Int,
    val categoriaId: Long,
    val tipo_lancamento: String? = null
)