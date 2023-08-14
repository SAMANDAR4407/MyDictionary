package uz.gita.mydictionary

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.InputStream

class DBHelper private constructor(private val context: Context) :
    SQLiteOpenHelper(context, "data", null, 1), DictionaryDao {

    companion object{
        private lateinit var instance: DBHelper

        fun getInstance(): DBHelper = instance

        fun init(context: Context){
            if (!(::instance.isInitialized))
                instance = DBHelper(context)
        }
    }

    private var db: SQLiteDatabase

    init {
        val file = context.getDatabasePath("dictionary.db")
        if (!file.exists()){
            copyToLocal()
        }
        db = SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private fun copyToLocal(){
        val inputStream: InputStream = context.assets.open("dictionary_external.db")
        val file = context.getDatabasePath("dictionary.db")
        val fileOutputStream = FileOutputStream(file)
        try {
            val byte = ByteArray(1024)
            var length = 0
            while (inputStream.read(byte).also { length = it } > 0){
                fileOutputStream.write(byte, 0, length)
            }
            fileOutputStream.flush()
        } catch (e: Exception){
            file.delete()
        } finally {
            inputStream.close()
            fileOutputStream.close()
        }
    }

    override fun getAllEnglishWords(): Cursor {
        return db.rawQuery("select * from dictionary order by english", null)
    }

    override fun getAllEnglishStarWords(): Cursor {
        return db.rawQuery("select * from dictionary where favourite=1", null)
    }

    override fun getAllUzbekWords(): Cursor {
        return db.rawQuery("select * from dictionary order by uzbek", null)
    }

    override fun getAllUzbekStarWords(): Cursor {
        return db.rawQuery("select * from dictionary where favourite=1", null)
    }

    override fun getAllStarredWords(): Cursor {
        return db.rawQuery("select * from dictionary where favourite=1", null)
    }

    override fun searchEnglish(word: String): Cursor {
        return db.rawQuery("select * from dictionary where english like \"$word%\"", null)
    }

    override fun searchUzbek(word: String): Cursor {
        return db.rawQuery("select * from dictionary where uzbek like \"$word%\"", null)
    }

    @SuppressLint("Recycle")
    override fun addFavourite(id: Int) {
        db.execSQL("update dictionary set favourite=1 where id=$id")
    }

    @SuppressLint("Recycle")
    override fun removeFavourite(id: Int) {
        db.execSQL("update dictionary set favourite=0 where id=$id")
    }
}