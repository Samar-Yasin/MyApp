package com.example.myapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsersInfo::class], version = 1)
abstract class MyAppDataBase : RoomDatabase() {

    abstract fun userInfoDao() : UserInfoDao

}