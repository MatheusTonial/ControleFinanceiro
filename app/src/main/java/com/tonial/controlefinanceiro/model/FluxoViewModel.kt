package com.tonial.controlefinanceiro.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.Contas
import com.tonial.controlefinanceiro.entity.TipoCategoria
import java.time.LocalDate

class FluxoViewModel: ViewModel() {

    //region Categoria

    var descricao_categoria by mutableStateOf("")
    var tipo_categoria by mutableStateOf(TipoCategoria.Perda)
    var ordem_categoria by mutableStateOf(99)

    fun onDescricaoCategoriaChange(newValue: String) {
        descricao_categoria = newValue
    }

    fun onTipoCategoriaChange(newValue: TipoCategoria) {
        tipo_categoria = newValue
    }

    fun onOrdemCategoriaChange(newValue: Int) {
        ordem_categoria = newValue
    }

    fun limpaCategoria(){
        descricao_categoria = ""
        tipo_categoria = TipoCategoria.Perda
        ordem_categoria = 99
    }

    fun salvarCategoria(db: DatabaseHandler) {
        val novaCategoria = Categorias(
            _id = 0, // O ID será gerado automaticamente pelo banco
            descricao = descricao_categoria,
            tipo = tipo_categoria,
            ordem = ordem_categoria
        )
        db.addCategoria(novaCategoria)
        limpaCategoria()
    }

    //endregion Categoria

    //region Conta

    var descricao_conta by mutableStateOf("")
    var valor_conta by mutableStateOf(0.0)
    var data_conta by mutableStateOf(LocalDate.now())
    var idRecorrente_conta by mutableStateOf(0)
    var categoria_id_conta by mutableStateOf<Long?>(null)
    var mensagemErro by mutableStateOf<String?>(null)

    fun onDescricaoContaChange(newValue: String) {
        descricao_conta = newValue
    }

    fun onValorContaChange(newValue: Double) {
        valor_conta = newValue
    }

    fun onDataContaChange(newValue: LocalDate) {
        data_conta = newValue
    }

    fun onIdRecorrenteContaChange(newValue: Int) {
        idRecorrente_conta = newValue
    }

    fun onCategoriaIdContaChange(newValue: Long?) {
        categoria_id_conta = newValue
    }

    fun limpaConta(){
        descricao_conta = ""
        valor_conta = 0.0
        data_conta = LocalDate.now()
        idRecorrente_conta = 0
        categoria_id_conta = null
        mensagemErro = null
    }

    fun salvarConta(db: DatabaseHandler): Boolean {
        if (descricao_conta.isBlank() || valor_conta == 0.0 || categoria_id_conta == null) {
            mensagemErro = "Preencha todos os campos obrigatórios."
            return false
        }

        val novaConta = Contas(
            _id = 0, // O ID será gerado automaticamente pelo banco
            descricao = descricao_conta,
            valor = valor_conta,
            data = data_conta,
            idRecorrente = idRecorrente_conta,
            categoriaId = categoria_id_conta!!
        )
        db.addConta(novaConta)
        limpaConta()
        return true
    }

    //endregion Conta

}