package com.example.myapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UsersInfo")
data class UsersInfo(
    var completeName: String,
    var email: String,
    val password : String,

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0

)
