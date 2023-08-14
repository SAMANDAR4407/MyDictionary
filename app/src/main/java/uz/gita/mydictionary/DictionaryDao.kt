package uz.gita.mydictionary

import android.database.Cursor

interface DictionaryDao {
    fun getAllEnglishWords(): Cursor
    fun getAllEnglishStarWords(): Cursor
    fun getAllUzbekWords(): Cursor
    fun getAllUzbekStarWords(): Cursor
    fun getAllStarredWords(): Cursor
    fun searchEnglish(word: String): Cursor
    fun searchUzbek(word: String): Cursor
    fun addFavourite(id: Int)
    fun removeFavourite(id: Int)
}