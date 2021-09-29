package com.example.travelerweatherapp.Data

// How to get icon URL
//For code 500 - light rain icon = "10d". See below a full list of codes
//URL is http://openweathermap.org/img/wn/{icon}@2x.png
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class Weather (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val country: String, // Sys.country
    val name: String,
    val lat: Double, // coord.lat
    val lon: Double, // coord.lon
    val main: String, // weather.main - Weather condition codes
    val description: String, // weather.description - Weather condition description
    val icon: String, // weather.icon
    val humidity: Int, // main.humidity
    val temp: Double, // main.temp
    val wind: Double,
    val clouds: Int,
    val dt: Int,
    val timezone: Int,
    val isLocal: Boolean
        )