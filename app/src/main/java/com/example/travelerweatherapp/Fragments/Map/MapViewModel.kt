package com.example.travelerweatherapp.Fragments.Map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.travelerweatherapp.Data.AppDb
import com.example.travelerweatherapp.Data.Weather
import com.example.travelerweatherapp.Data.WeatherRepository

class MapViewModel(application: Application) : AndroidViewModel(application) {

    val getAllLiveData: LiveData<List<Weather>>
    private val repository: WeatherRepository

    init {
        val weatherDao = AppDb.getDatabase(application).weatherDao()
        repository = WeatherRepository(weatherDao)
        getAllLiveData = repository.getAllLiveData
    }

    fun getWeatherLocal(): Weather{
        return repository.getWeatherLocal()
    }

    fun getWeatherCapitals(): List<Weather> = repository.getWeatherCapitals()
}