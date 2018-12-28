package com.ballboycorp.blabs.roomextension.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ballboycorp.blabs.roomextension.room.dao.UserDao
import com.ballboycorp.blabs.roomextension.room.model.School
import com.ballboycorp.blabs.roomextension.room.model.Student
import com.ballboycorp.blabs.roomextension.room.model.User


/**
 * Created by muso on 27/12/2018.
 */
@Database(
    entities = [
        User::class,
        Student::class,
        School::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this){
                    INSTANCE
                            ?: Room.databaseBuilder(context, AppDatabase::class.java, "appdatabase.db")
                        .addMigrations(MIGRATION_1_2)
                        .build()
                        .also {
                            INSTANCE = it
                        }
                }

        private val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
}