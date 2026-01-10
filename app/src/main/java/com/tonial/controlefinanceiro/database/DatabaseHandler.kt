package com.tonial.controlefinanceiro.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.Contas
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.UltimoLancamento
import java.math.BigDecimal
import java.time.LocalDate

// Classe de acesso ao banco de dados SQLite do aplicativo.
class DatabaseHandler private constructor(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bdfile.sqlite"
        private const val DATABASE_VERSION = 3

        @Volatile
        private var INSTANCE: DatabaseHandler? = null

        // Garante que apenas uma instância do DatabaseHandler seja criada (Singleton).
        fun getInstance(context: Context): DatabaseHandler {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseHandler(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        // Tabela Categorias
        private const val TABLE_CATEGORIAS = "categorias"
        private const val KEY_ID_CATEGORIA = "_id"
        private const val KEY_DESCRICAO_CATEGORIA = "descricao"
        private const val KEY_TIPO_CATEGORIA = "tipo"
        private const val KEY_ORDEM_CATEGORIA = "ordem"

        // Tabela Contas
        private const val TABLE_CONTAS = "contas"
        private const val KEY_ID_CONTA = "_id"
        private const val KEY_DESCRICAO_CONTA = "descricao"
        private const val KEY_VALOR_CONTA = "valor"
        private const val KEY_DATA_CONTA = "data"
        private const val KEY_ID_RECORRENTE_CONTA = "idRecorrente"
        private const val KEY_CATEGORIA_CONTA = "categoria_id"
    }

    // Cria as tabelas do banco de dados na primeira execução.
    override fun onCreate(db: SQLiteDatabase?) {
        val createCategoriaTable = "CREATE TABLE $TABLE_CATEGORIAS(" +
                "$KEY_ID_CATEGORIA INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_DESCRICAO_CATEGORIA TEXT," +
                "$KEY_TIPO_CATEGORIA TEXT," +
                "$KEY_ORDEM_CATEGORIA INTEGER)"
        db?.execSQL(createCategoriaTable)

        // Inseri algumas categorias iniciais para perda.
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Mercado', 'Perda', 0)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Alimentação', 'Perda', 1)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Assinaturas', 'Perda', 2)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Condominio', 'Perda', 3)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Internet', 'Perda', 4)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Lazer', 'Perda', 5)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Luz', 'Perda', 6)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Pessoal', 'Perda', 7)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Saude', 'Perda', 8)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Transporte', 'Perda', 9)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Investimentos', 'Perda', 10)")
        db?.execSQL("INSERT INTO $TABLE_CATEGORIAS ($KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) VALUES ('Outros', 'Perda', 11)")


        val createContaTable = "CREATE TABLE $TABLE_CONTAS(" +
                "$KEY_ID_CONTA INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_DESCRICAO_CONTA TEXT," +
                "$KEY_VALOR_CONTA REAL," +
                "$KEY_DATA_CONTA TEXT," +
                "$KEY_ID_RECORRENTE_CONTA INTEGER," +
                "$KEY_CATEGORIA_CONTA INTEGER," +
                "FOREIGN KEY($KEY_CATEGORIA_CONTA) REFERENCES $TABLE_CATEGORIAS($KEY_ID_CATEGORIA))"
        db?.execSQL(createContaTable)
    }

    // Executa migrações do banco de dados ao atualizar a versão.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int ) {
        if (oldVersion < 2) {
            // Exemplo: db?.execSQL("ALTER TABLE $TABLE_CATEGORIAS ADD COLUMN nova_coluna TEXT;")
        }
    }

    // Adiciona uma nova categoria ao banco de dados.
    fun addCategoria(categoria: Categorias): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CATEGORIA, categoria.descricao)
            put(KEY_TIPO_CATEGORIA, categoria.tipo.name)
            put(KEY_ORDEM_CATEGORIA, categoria.ordem)
        }
        return db.insert(TABLE_CATEGORIAS, null, values)
    }
    
    // Atualiza uma categoria existente no banco de dados.
    fun updateCategoria(categoria: Categorias): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CATEGORIA, categoria.descricao)
            put(KEY_TIPO_CATEGORIA, categoria.tipo.name)
            put(KEY_ORDEM_CATEGORIA, categoria.ordem)
        }
        return db.update(TABLE_CATEGORIAS, values, "$KEY_ID_CATEGORIA = ?", arrayOf(categoria._id.toString()))
    }

    // Exclui uma categoria do banco de dados pelo seu ID.
    fun deleteCategoriaById(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_CATEGORIAS, "$KEY_ID_CATEGORIA = ?", arrayOf(id.toString()))
    }

    // Busca uma categoria pelo seu ID.
    fun getCategoriaById(id: Long): Categorias? {
        val db = this.readableDatabase
        var categoria: Categorias? = null
        db.rawQuery("SELECT * FROM $TABLE_CATEGORIAS WHERE $KEY_ID_CATEGORIA = ?", arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                categoria = Categorias(
                    _id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_CATEGORIA)),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CATEGORIA)),
                    tipo = TipoCategoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIPO_CATEGORIA))),
                    ordem = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ORDEM_CATEGORIA))
                )
            }
        }
        return categoria
    }

    // Retorna todas as categorias cadastradas, ordenadas pela ordem e ID.
    fun getAllCategorias(): List<Categorias> {
        val categoriasList = ArrayList<Categorias>()
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIAS ORDER BY $KEY_ORDEM_CATEGORIA ASC, $KEY_ID_CATEGORIA ASC"
        val db = this.readableDatabase
        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val categoria = Categorias(
                        _id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_CATEGORIA)),
                        descricao = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CATEGORIA)),
                        tipo = TipoCategoria.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIPO_CATEGORIA))),
                        ordem = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ORDEM_CATEGORIA))
                    )
                    categoriasList.add(categoria)
                } while (cursor.moveToNext())
            }
        }
        return categoriasList
    }

    // Adiciona uma nova conta (lançamento) ao banco de dados.
    fun addConta(conta: Contas): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CONTA, conta.descricao)
            put(KEY_VALOR_CONTA, conta.valor)
            put(KEY_DATA_CONTA, conta.data.toString())
            put(KEY_ID_RECORRENTE_CONTA, conta.idRecorrente)
            put(KEY_CATEGORIA_CONTA, conta.categoriaId)
        }
        return db.insert(TABLE_CONTAS, null, values)
    }

    // Atualiza uma conta existente no banco de dados.
    fun updateConta(conta: Contas): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CONTA, conta.descricao)
            put(KEY_VALOR_CONTA, conta.valor)
            put(KEY_DATA_CONTA, conta.data.toString())
            put(KEY_ID_RECORRENTE_CONTA, conta.idRecorrente)
            put(KEY_CATEGORIA_CONTA, conta.categoriaId)
        }
        return db.update(TABLE_CONTAS, values, "$KEY_ID_CONTA = ?", arrayOf(conta._id.toString()))
    }

    // Exclui um lançamento do banco de dados pelo seu ID.
    fun deleteLancamentoById(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_CONTAS, "$KEY_ID_CONTA = ?", arrayOf(id.toString()))
    }
    
    // Busca uma conta pelo seu ID.
    fun getContaById(id: Long): Contas? {
        val db = this.readableDatabase
        var conta: Contas? = null
        db.rawQuery("SELECT * FROM $TABLE_CONTAS WHERE $KEY_ID_CONTA = ?", arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                conta = Contas(
                    _id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_CONTA)),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CONTA)),
                    valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALOR_CONTA)),
                    data = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATA_CONTA))),
                    idRecorrente = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID_RECORRENTE_CONTA)),
                    categoriaId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CATEGORIA_CONTA))
                )
            }
        }
        return conta
    }

    // Retorna o total de gastos no mês atual.
    fun getTotalGastoMesAtual(): BigDecimal {
        val db = this.readableDatabase
        val query = """
            SELECT SUM(T2.$KEY_VALOR_CONTA)
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE T1.$KEY_TIPO_CATEGORIA = '${TipoCategoria.Perda.name}'
            AND strftime('%Y-%m', T2.$KEY_DATA_CONTA) = strftime('%Y-%m', 'now')
        """
        var total = BigDecimal.ZERO
        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                total = BigDecimal(cursor.getDouble(0))
            }
        }
        return total
    }

    // Retorna as 5 categorias com mais gastos no mês atual.
    fun getCategoriasMaisGastasMesAtual(): List<CategoriaMaisGasta> {
        val categorias = mutableListOf<CategoriaMaisGasta>()
        val db = this.readableDatabase
        val query = """
            SELECT T1.$KEY_DESCRICAO_CATEGORIA, SUM(T2.$KEY_VALOR_CONTA) as total, COUNT(T2.$KEY_ID_CONTA) as quantidade
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE T1.$KEY_TIPO_CATEGORIA = '${TipoCategoria.Perda.name}'
            AND strftime('%Y-%m', T2.$KEY_DATA_CONTA) = strftime('%Y-%m', 'now')
            GROUP BY T1.$KEY_ID_CATEGORIA
            ORDER BY total DESC
            LIMIT 5
        """
        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val categoria = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CATEGORIA))
                    val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                    val quantidade = cursor.getInt(cursor.getColumnIndexOrThrow("quantidade"))
                    categorias.add(CategoriaMaisGasta(categoria, BigDecimal(total), quantidade))
                } while (cursor.moveToNext())
            }
        }
        return categorias
    }

    // Retorna os 10 últimos lançamentos.
    fun getUltimosLancamentos(): List<UltimoLancamento> {
        val lancamentos = mutableListOf<UltimoLancamento>()
        val db = this.readableDatabase
        val query = """
            SELECT 
                T2.$KEY_ID_CONTA, 
                T2.$KEY_DESCRICAO_CONTA AS lancamento_descricao, 
                T1.$KEY_DESCRICAO_CATEGORIA AS categoria_descricao, 
                T2.$KEY_VALOR_CONTA, 
                T2.$KEY_DATA_CONTA, 
                T1.$KEY_TIPO_CATEGORIA
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE date(T2.$KEY_DATA_CONTA) <= date('now')
            ORDER BY T2.$KEY_DATA_CONTA DESC
            LIMIT 10
        """
        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_CONTA))
                    val descricao = cursor.getString(cursor.getColumnIndexOrThrow("lancamento_descricao"))
                    val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria_descricao"))
                    val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALOR_CONTA))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATA_CONTA))
                    val tipo = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIPO_CATEGORIA))
                    lancamentos.add(UltimoLancamento(id, descricao, categoria, BigDecimal(valor), data, tipo))
                } while (cursor.moveToNext())
            }
        }
        return lancamentos
    }
    
    // Retorna o histórico de lançamentos com base nos filtros de data e categoria.
    fun getHistorico(
    dataInicio: String,
    dataFim: String,
    categoriaId: Long? = null
): List<UltimoLancamento> {
    val lancamentos = mutableListOf<UltimoLancamento>()
    val db = this.readableDatabase
    var query = """
        SELECT
            T2.$KEY_ID_CONTA,
            T2.$KEY_DESCRICAO_CONTA AS lancamento_descricao,
            T1.$KEY_DESCRICAO_CATEGORIA AS categoria_descricao,
            T2.$KEY_VALOR_CONTA,
            T2.$KEY_DATA_CONTA,
            T1.$KEY_TIPO_CATEGORIA
        FROM $TABLE_CATEGORIAS T1
        INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
        WHERE date(T2.$KEY_DATA_CONTA) BETWEEN date(?) AND date(?)
    """

    val selectionArgs = mutableListOf(dataInicio, dataFim)

    if (categoriaId != null) {
        query += " AND T1.$KEY_ID_CATEGORIA = ?"
        selectionArgs.add(categoriaId.toString())
    }

    query += " ORDER BY T2.$KEY_DATA_CONTA DESC"

    db.rawQuery(query, selectionArgs.toTypedArray()).use { cursor ->
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_CONTA))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow("lancamento_descricao"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria_descricao"))
                val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALOR_CONTA))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATA_CONTA))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIPO_CATEGORIA))
                lancamentos.add(UltimoLancamento(id, descricao, categoria, BigDecimal(valor), data, tipo))
            } while (cursor.moveToNext())
        }
    }
    return lancamentos
}
}