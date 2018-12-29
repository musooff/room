package com.ballboycorp.blabs.roomextensionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ballboycorp.blabs.roomextensionexample.room.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appDatabase = AppDatabase.getInstance(applicationContext)
        val result = appDatabase.schoolDao().getAll()

        val forDebugging = ""

    }
}
