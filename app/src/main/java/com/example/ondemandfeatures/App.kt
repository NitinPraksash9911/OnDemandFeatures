package com.example.ondemandfeatures

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

class App : Application() {


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object{
        private var instance: Context? = null
        fun getInstance()= instance!!.applicationContext!!
    }


}