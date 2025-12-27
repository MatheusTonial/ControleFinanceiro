package com.tonial.controlefinanceiro.entity

data class Categorias (
    val _id: Long,
    val descricao: String,
    val cor: String,
    val tipo: TipoCategoria,
    val ordem: Int
)



