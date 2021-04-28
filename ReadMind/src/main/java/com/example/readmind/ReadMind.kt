package com.example.readmind

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
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


            fun launch(context: Context, vararg flags: Int) {
                val intent = getIntent(context, *flags)
                context.startActivity(intent)
            }

            fun getIntent(context: Context?, vararg flags: Int): Intent {
                val intent = Intent(context, ReadMind::class.java)
                for (element in flags) {
                    intent.addFlags(element)
                }
                return intent
            }


    }

}