package com.tonial.controlefinanceiro.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.Contas
import com.tonial.controlefinanceiro.entity.TipoCategoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FluxoViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHandler = DatabaseHandler.getInstance(application)

    //region Categoria
    var categoriaId by mutableStateOf<Long?>(null)
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
        categoriaId = null
        descricao_categoria = ""
        tipo_categoria = TipoCategoria.Perda
        ordem_categoria = 99
    }

    fun loadCategoria(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.getCategoriaById(id)?.let { categoria ->
                categoriaId = categoria._id
                descricao_categoria = categoria.descricao
                tipo_categoria = categoria.tipo
                ordem_categoria = categoria.ordem
            }
        }
    }

    fun salvarCategoria() {
        viewModelScope.launch(Dispatchers.IO) {
            val categoria = Categorias(
                _id = categoriaId ?: 0,
                descricao = descricao_categoria,
                tipo = tipo_categoria,
                ordem = ordem_categoria
            )
            if (categoriaId == null) {
                dbHandler.addCategoria(categoria)
            } else {
                dbHandler.updateCategoria(categoria)
            }
            limpaCategoria()
        }
    }

    //endregion Categoria

    //region Conta

    var contaId by mutableStateOf<Long?>(null)
    var descricao_conta by mutableStateOf("")
    var valor_conta by mutableStateOf(0.0)
    var data_conta by mutableStateOf(LocalDate.now())
    var idRecorrente_conta by mutableStateOf(0)
    var categoria_id_conta by mutableStateOf<Long?>(null)
    var lancamentoUnico_conta by mutableStateOf(false)
    var mensagemErro by mutableStateOf<String?>(null)

    fun loadConta(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHandler.getContaById(id)?.let { conta ->
                contaId = conta._id
                descricao_conta = conta.descricao
                valor_conta = conta.valor
                data_conta = conta.data
                idRecorrente_conta = conta.idRecorrente
                categoria_id_conta = conta.categoriaId
                lancamentoUnico_conta = conta.tipo_lancamento == DatabaseHandler.TIPO_LANCAMENTO_UNICO
            }
        }
    }

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
    
    fun onLancamentoUnicoContaChange(newValue: Boolean) {
        lancamentoUnico_conta = newValue
    }

    fun limpaConta(){
        contaId = null
        descricao_conta = ""
        valor_conta = 0.0
        data_conta = LocalDate.now()
        idRecorrente_conta = 0
        categoria_id_conta = null
        lancamentoUnico_conta = false
        mensagemErro = null
    }

    fun salvarConta(): Boolean {
        if (descricao_conta.isBlank() || valor_conta == 0.0 || categoria_id_conta == null) {
            mensagemErro = "Preencha todos os campos obrigatórios."
            return false
        }

        viewModelScope.launch(Dispatchers.IO) {
            val tipoLancamento = if (lancamentoUnico_conta) DatabaseHandler.TIPO_LANCAMENTO_UNICO else null
            val conta = Contas(
                _id = contaId ?: 0,
                descricao = descricao_conta,
                valor = valor_conta,
                data = data_conta,
                idRecorrente = idRecorrente_conta,
                categoriaId = categoria_id_conta!!,
                tipo_lancamento = tipoLancamento
            )
            if (contaId == null) {
                dbHandler.addConta(conta)
            } else {
                dbHandler.updateConta(conta)
            }
            limpaConta()
        }
        return true
    }

    //endregion Conta

}
