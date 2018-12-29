package com.ballboycorp.blabs.roomextensionexample.room.model

import androidx.room.Entity


/**
 * Created by muso on 28/12/2018.
 */
@Entity(primaryKeys = ["number", "students"])
class School {
    var number: Int = 0
    var name: String? = null
    var students: Long = 0
    var principal: String? = null
    var isScience: Boolean = false
}