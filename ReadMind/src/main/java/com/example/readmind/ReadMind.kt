package com.example.readmind

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.splitcompat.SplitCompat

class ReadMind : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_mind)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    companion object {

        fun dynamicMethod() {
            Log.d("hurray", "dynamicMethod: ${ReadMind::class.simpleName}")
        }
    }

}