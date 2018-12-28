package com.ballboycorp.blabs.roomextension.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.ballboycorp.blabs.roomextension.room.model.User
import io.reactivex.Observable


/**
 * Created by muso on 27/12/2018.
 */

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Observable<List<User>>
}