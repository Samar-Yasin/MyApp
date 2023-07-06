package com.example.myapp

import androidx.room.*

@Dao
interface UserInfoDao {

    @Query("SELECT * FROM UsersInfo WHERE email = :emails")
     suspend fun getUserInfoByEmail(emails: String): UsersInfo

    @Query("UPDATE UsersInfo SET password = :newPassword WHERE email = :email")
    suspend fun updatePassword(email: String, newPassword: String)

    @Insert
    suspend fun insertUserInfo(userInfo: UsersInfo)

    @Update
    suspend fun updateUserInfo(userInfo: UsersInfo)

    @Delete
    suspend fun deleterUserInfo(userInfo: UsersInfo)

}