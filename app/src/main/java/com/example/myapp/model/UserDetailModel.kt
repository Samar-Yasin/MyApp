package com.example.myapp.model

data class UserDetailModel(
    val completeName: String = "",
    val email: String = "",
    val id: String = "",
    val loginDone: Boolean = false,
    val profilePicURL: String = ""
)