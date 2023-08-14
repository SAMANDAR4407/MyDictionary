package uz.gita.mydictionary

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DBHelper.init(this)
    }
}