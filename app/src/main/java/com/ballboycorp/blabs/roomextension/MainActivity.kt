package com.ballboycorp.blabs.roomextension

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ballboycorp.blabs.roomextension.room.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appDatabase = AppDatabase.getInstance(applicationContext)
        val result = appDatabase.schoolDao().getAll()

        val forDebugging = ""

    }
}
