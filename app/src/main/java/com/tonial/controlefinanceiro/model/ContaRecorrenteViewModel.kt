package com.tonial.controlefinanceiro.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.entity.Contas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ContaRecorrenteViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHandler = DatabaseHandler.getInstance(application)

    var descricao by mutableStateOf("")
        private set

    var valor by mutableStateOf(0.0)
        private set

    var data by mutableStateOf(LocalDate.now())
        private set

    var categoriaId by mutableStateOf<Long?>(null)
        private set

    var mensagemErro by mutableStateOf<String?>(null)
        private set

    private var contaId: Long? = null

    fun onDescricaoChange(newDescricao: String) {
        descricao = newDescricao
    }

    fun onValorChange(newValor: Double) {
        valor = newValor
    }

    fun onDataChange(newData: LocalDate) {
        data = newData
    }

    fun onCategoriaIdChange(newCategoriaId: Long) {
        categoriaId = newCategoriaId
    }

    fun salvarConta(): Boolean {
        if (descricao.isBlank() || valor <= 0 || categoriaId == null) {
            mensagemErro = "Preencha todos os campos obrigatórios."
            return false
        }

        val conta = Contas(
            _id = contaId ?: 0,
            descricao = descricao,
            valor = valor,
            data = data,
            categoriaId = categoriaId!!,
            tipo_lancamento = DatabaseHandler.TIPO_LANCAMENTO_RECORRENTE
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (contaId == null) {
                dbHandler.addGastoRecorrente(conta)
            } else {
                dbHandler.updateGastoRecorrente(conta)
            }
        }
        return true
    }

    fun loadConta(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val conta = dbHandler.getGastoRecorrenteById(id)
            if (conta != null) {
                contaId = conta._id
                descricao = conta.descricao
                valor = conta.valor
                data = conta.data
                categoriaId = conta.categoriaId
            }
        }
    }

    fun limpaConta() {
        contaId = null
        descricao = ""
        valor = 0.0
        data = LocalDate.now()
        categoriaId = null
    }
}