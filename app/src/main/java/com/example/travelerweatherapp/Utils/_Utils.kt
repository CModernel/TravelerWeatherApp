package com.example.travelerweatherapp.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.travelerweatherapp.BuildConfig.API_KEY
import com.example.travelerweatherapp.Data.Weather
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class _Utils {

    companion object {
        private var lastLatitude: Double = 0.0
        private var lastLongitud: Double = 0.0
        var localTime: Int = -10800 // Default= UY LocalTime Zone
        private lateinit var latlngCapitals: Map<Int, LatLng>

        // TODO: crear funcion para url builder con lat lon
        // api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
        private fun forecast_url_builder_location(lat: Double, lon: Double): URL {
            val FORECAST_BASE_URL =
                "https://api.openweathermap.org/data/2.5/weather?"
            val LAT_PARAM = "lat"
            val LON_PARAM = "lon"
            val UNITS_PARAM = "units"
            val APPID_PARAM = "appid"
            val builtUri: Uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, lat.toString())
                .appendQueryParameter(LON_PARAM, lon.toString())
                .appendQueryParameter(UNITS_PARAM, "metric")
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()
            val url = URL(builtUri.toString())
            return url
        }

        private fun forecast_url_builder_city(cityName: String): URL {
            val FORECAST_BASE_URL =
                "https://api.openweathermap.org/data/2.5/weather?"
            val QUERY_PARAM = "q"
            val UNITS_PARAM = "units"
            val APPID_PARAM = "appid"
            val builtUri: Uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, cityName)
                .appendQueryParameter(UNITS_PARAM, "metric")
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()
            val url = URL(builtUri.toString())
            return url
        }

        private fun forecast_url_builder_city_id(cityId: Int): URL {
            val FORECAST_BASE_URL =
                "https://api.openweathermap.org/data/2.5/weather?"
            val ID_PARAM = "id"
            val UNITS_PARAM = "units"
            val APPID_PARAM = "appid"
            val builtUri: Uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(ID_PARAM, cityId.toString())
                .appendQueryParameter(UNITS_PARAM, "metric")
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()
            val url = URL(builtUri.toString())
            return url
        }

        fun runRequestJson(cityName: String): JSONObject {
            val url = forecast_url_builder_city(cityName)
            val resultJson = URL(url.toString()).readText()

            return JSONObject(resultJson)
        }

        fun runRequestJson(lat: Double, lon: Double): JSONObject {
            val url = forecast_url_builder_location(lat, lon)
            val resultJson = URL(url.toString()).readText()

            return JSONObject(resultJson)
        }

        fun runRequestJson(cityId: Int): JSONObject {
            val url = forecast_url_builder_city_id(cityId)
            val resultJson = URL(url.toString()).readText()

            return JSONObject(resultJson)
        }

        fun weatherBuilder(json: JSONObject, isLocal: Boolean): Weather {
            val id: Int = json.getInt("id")
            val country: String = json.getJSONObject("sys").getString("country") // Sys.country
            val name: String = json.getString("name")
            val lat: Double = json.getJSONObject("coord").getDouble("lat")// coord.lat
            val lon: Double = json.getJSONObject("coord").getDouble("lon") // coord.lon

            val weatherObj: JSONObject = (json.getJSONArray("weather")[0] as JSONObject)
            val main: String =
                weatherObj.getString("main") // weather.main - Weather condition codes
            val description: String =
                weatherObj.getString("description") // weather.description - Weather condition description
            val icon: String = weatherObj.getString("icon") // weather.icon

            val humidity: Int = json.getJSONObject("main").getInt("humidity") // main.humidity
            val temp: Double = json.getJSONObject("main").getDouble("temp") // main.temp

            val wind: Double = json.getJSONObject("wind").getDouble("speed")
            val clouds: Int = json.getJSONObject("clouds").getInt("all")

            val dt: Int = json.getInt("dt")
            val timezone: Int = json.getInt("timezone")

            val newWeather = Weather(
                id,
                country,
                name,
                lat,
                lon,
                main,
                description,
                icon,
                humidity,
                temp,
                wind,
                clouds,
                dt,
                timezone,
                isLocal
            )
            return newWeather
        }

        fun getDateTime(s: String): String? {
            try {
                val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                val netDate = Date(s.toLong() * 1000)
                return sdf.format(netDate)
            } catch (e: Exception) {
                return e.toString()
            }
        }

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

        fun getAllCapitals(): Map<Int, LatLng> {
            if (!::latlngCapitals.isInitialized || latlngCapitals.size == 0)
                createAllCapitals()

            return latlngCapitals
        }

        fun createAllCapitals(){
            latlngCapitals = mapOf(
                2867714 to LatLng(48.1374,11.5755), // Munich
                1850147 to LatLng(35.6895,139.6917), // Tokio
                1070940 to LatLng(-18.9137,47.5361), // Antananarivo
                5128581 to LatLng(40.7143,-74.006), // Nueva York
                2147714 to LatLng(-33.8679, 151.2073), // Sydney
                3441575 to LatLng(-34.8335, -56.1674) // Montevideo
            )
        }
    }
}