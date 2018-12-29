package com.ballboycorp.blabs.roomextension.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ballboycorp.blabs.roomextension.room.model.School


/**
 * Created by muso on 27/12/2018.
 */

@Dao
interface SchoolDao {
    @Query("SELECT * FROM School")
    fun getAll(): List<School>
}