package com.example.travelerweatherapp.Fragments.Register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelerweatherapp.Data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application){
    private val repository: UserRepository

    init {
        val userDao = AppDb.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun getCountUsers(): Int{
        return repository.getCountUsers()
    }
}