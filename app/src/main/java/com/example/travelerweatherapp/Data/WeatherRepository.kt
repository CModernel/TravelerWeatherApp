package com.example.travelerweatherapp.Data

import androidx.lifecycle.LiveData

class WeatherRepository(private val weatherDao: WeatherDao) {

    val getAllLiveData: LiveData<List<Weather>> = weatherDao.getAllLiveData()

    suspend fun addWeather(weather: Weather){
        weatherDao.insert(weather)
    }

    fun getWeatherLocal(): Weather{
        return weatherDao.getLocal()
    }

    fun getWeatherCapitals(): List<Weather> = weatherDao.getAllCapitals()

}