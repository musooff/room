
package com.ballboycorp.blabs.roomextension.room.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import javax.annotation.Nullable


/**
 * Created by muso on 27/12/2018.
 */
@Entity(foreignKeys = [(ForeignKey(entity = School::class, parentColumns = arrayOf("students", "number"), childColumns = arrayOf("id", "name"), onDelete = ForeignKey.CASCADE))])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val lastName: String?
)