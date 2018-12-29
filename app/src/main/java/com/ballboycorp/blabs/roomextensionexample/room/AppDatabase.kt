package com.ballboycorp.blabs.roomextensionexample.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ballboycorp.blabs.roomextensionexample.room.dao.SchoolDao
import com.ballboycorp.blabs.roomextensionexample.room.model.*


/**
 * Created by muso on 27/12/2018.
 */
@Database(
    entities = [
        School::class,
        User::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun schoolDao(): SchoolDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, "appdatabase.db")
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .allowMainThreadQueries()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(StudentSqlUtils().createTable)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(StudentSqlUtils().dropTable)
                database.execSQL(UserSqlUtils().createTable)
            }
        }
    }
}