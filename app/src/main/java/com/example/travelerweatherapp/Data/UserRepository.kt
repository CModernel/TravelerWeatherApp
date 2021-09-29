package com.example.travelerweatherapp.Data

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

class UserRepository(private val userDao: UserDao) {

    fun getAll(): List<User> {
        return userDao.getAll()
    }

    suspend fun addUser(user: User){
        userDao.insert(user)
    }

    fun getCountUsers(): Int{
        return userDao.countUsers()
    }
}