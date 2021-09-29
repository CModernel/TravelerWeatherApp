package com.example.travelerweatherapp.Fragments.WeatherCurrent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.travelerweatherapp.Data.Weather
import com.example.travelerweatherapp.Data.AppDb
import com.example.travelerweatherapp.Data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    val getAllLiveData: LiveData<List<Weather>>
    private val repository: WeatherRepository

    init {
        val weatherDao = AppDb.getDatabase(application).weatherDao()
        repository = WeatherRepository(weatherDao)
        getAllLiveData = repository.getAllLiveData
    }

    fun addWeather(weather: Weather){
        viewModelScope.launch(Dispatchers.IO){
            repository.addWeather(weather)
        }
    }

    fun getWeatherLocal(): Weather{
            return repository.getWeatherLocal()
    }
}