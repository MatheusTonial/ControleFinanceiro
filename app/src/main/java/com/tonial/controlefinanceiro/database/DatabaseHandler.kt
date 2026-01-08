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

class DatabaseHandler (context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "bdfile.sqlite"
        const val DATABASE_VERSION = 3

        // Tabela Categorias
        const val TABLE_CATEGORIAS = "categorias"
        const val KEY_ID_CATEGORIA = "_id"
        const val KEY_DESCRICAO_CATEGORIA = "descricao"
        const val KEY_TIPO_CATEGORIA = "tipo"
        const val KEY_ORDEM_CATEGORIA = "ordem"

        // Tabela Contas
        const val TABLE_CONTAS = "contas"
        const val KEY_ID_CONTA = "_id"
        const val KEY_DESCRICAO_CONTA = "descricao"
        const val KEY_VALOR_CONTA = "valor"
        const val KEY_DATA_CONTA = "data"
        const val KEY_ID_RECORRENTE_CONTA = "idRecorrente"
        const val KEY_CATEGORIA_CONTA = "categoria_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createCategoriaTable = "CREATE TABLE $TABLE_CATEGORIAS(" +
                "$KEY_ID_CATEGORIA INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_DESCRICAO_CATEGORIA TEXT," +
                "$KEY_TIPO_CATEGORIA TEXT," +
                "$KEY_ORDEM_CATEGORIA INTEGER)"
        db?.execSQL(createCategoriaTable)

        // Inserir categorias iniciais
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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int ) {
        //if para aplicar as alterações de forma incremental a cada nova versão do banco de dados.
        if (oldVersion < 2) {
            //db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN email TEXT;")
        }
        if (oldVersion < 3) {
            val createCategoriaTableNew = "CREATE TABLE ${TABLE_CATEGORIAS}_new(" +
                    "$KEY_ID_CATEGORIA INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_DESCRICAO_CATEGORIA TEXT," +
                    "$KEY_TIPO_CATEGORIA TEXT," +
                    "$KEY_ORDEM_CATEGORIA INTEGER)"
            db?.execSQL(createCategoriaTableNew)

            db?.execSQL("INSERT INTO ${TABLE_CATEGORIAS}_new ($KEY_ID_CATEGORIA, $KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA) SELECT $KEY_ID_CATEGORIA, $KEY_DESCRICAO_CATEGORIA, $KEY_TIPO_CATEGORIA, $KEY_ORDEM_CATEGORIA FROM $TABLE_CATEGORIAS")

            db?.execSQL("DROP TABLE $TABLE_CATEGORIAS")

            db?.execSQL("ALTER TABLE ${TABLE_CATEGORIAS}_new RENAME TO $TABLE_CATEGORIAS")
        }
    }

    fun addCategoria(categoria: Categorias): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CATEGORIA, categoria.descricao)
            put(KEY_TIPO_CATEGORIA, categoria.tipo.name)
            put(KEY_ORDEM_CATEGORIA, categoria.ordem)
        }
        val id = db.insert(TABLE_CATEGORIAS, null, values)
        db.close()
        return id
    }

    fun getAllCategorias(): List<Categorias> {
        val categoriasList = ArrayList<Categorias>()
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIAS ORDER BY $KEY_ORDEM_CATEGORIA ASC, $KEY_ID_CATEGORIA ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
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
        cursor.close()
        db.close()
        return categoriasList
    }

    fun addConta(conta: Contas): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESCRICAO_CONTA, conta.descricao)
            put(KEY_VALOR_CONTA, conta.valor)
            put(KEY_DATA_CONTA, conta.data.toString())
            put(KEY_ID_RECORRENTE_CONTA, conta.idRecorrente)
            put(KEY_CATEGORIA_CONTA, conta.categoriaId)
        }
        val id = db.insert(TABLE_CONTAS, null, values)
        db.close()
        return id
    }
    
    fun getTotalGastoMesAtual(): BigDecimal {
        val db = this.readableDatabase
        val query = """
            SELECT SUM(T2.$KEY_VALOR_CONTA)
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE T1.$KEY_TIPO_CATEGORIA = '${TipoCategoria.Perda.name}'
            AND strftime('%Y-%m', T2.$KEY_DATA_CONTA) = strftime('%Y-%m', 'now')
        """
        val cursor = db.rawQuery(query, null)
        var total = BigDecimal.ZERO
        if (cursor.moveToFirst()) {
            total = BigDecimal(cursor.getDouble(0))
        }
        cursor.close()
        db.close()
        return total
    }

    fun getCategoriasMaisGastasMesAtual(): List<CategoriaMaisGasta> {
        val categorias = mutableListOf<CategoriaMaisGasta>()
        val db = this.readableDatabase
        val query = """
            SELECT T1.$KEY_DESCRICAO_CATEGORIA, SUM(T2.$KEY_VALOR_CONTA) as total
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE T1.$KEY_TIPO_CATEGORIA = '${TipoCategoria.Perda.name}'
            AND strftime('%Y-%m', T2.$KEY_DATA_CONTA) = strftime('%Y-%m', 'now')
            GROUP BY T1.$KEY_ID_CATEGORIA
            ORDER BY total DESC
            LIMIT 5
        """
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CATEGORIA))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                categorias.add(CategoriaMaisGasta(categoria, BigDecimal(total)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categorias
    }

    fun getUltimosLancamentos(): List<UltimoLancamento> {
        val lancamentos = mutableListOf<UltimoLancamento>()
        val db = this.readableDatabase
        val query = """
            SELECT T2.$KEY_DESCRICAO_CONTA, T1.$KEY_DESCRICAO_CATEGORIA, T2.$KEY_VALOR_CONTA, T2.$KEY_DATA_CONTA, T1.$KEY_TIPO_CATEGORIA
            FROM $TABLE_CATEGORIAS T1
            INNER JOIN $TABLE_CONTAS T2 ON T1.$KEY_ID_CATEGORIA = T2.$KEY_CATEGORIA_CONTA
            WHERE date(T2.$KEY_DATA_CONTA) <= date('now')
            ORDER BY T2.$KEY_DATA_CONTA DESC
            LIMIT 5
        """
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CONTA))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO_CATEGORIA))
                val valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALOR_CONTA))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATA_CONTA))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIPO_CATEGORIA))
                lancamentos.add(UltimoLancamento(descricao, categoria, BigDecimal(valor), data, tipo))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lancamentos
    }
}
