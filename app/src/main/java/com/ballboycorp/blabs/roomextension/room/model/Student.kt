package com.ballboycorp.blabs.roomextension.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by muso on 27/12/2018.
 */
@Entity(tableName = "roomStudent")
data class Student(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "student_name") val name: String,
    val age: Long,
    val gpa: Float,
    val test: Double,
    val surname: String?,
    val isGood: Boolean
)