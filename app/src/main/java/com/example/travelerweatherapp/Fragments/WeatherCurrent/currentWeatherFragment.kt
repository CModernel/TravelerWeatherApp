package com.example.travelerweatherapp.Fragments.WeatherCurrent

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.travelerweatherapp.Data.Weather
import com.example.travelerweatherapp.R
import com.example.travelerweatherapp.Utils._Utils
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.current_weather_fragment.view.*
import org.jetbrains.anko.custom.async
import java.lang.Exception

class currentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = currentWeatherFragment()
    }

    private lateinit var mWeatherViewModel: CurrentWeatherViewModel
    lateinit var mFusedLocationProviderCliente: FusedLocationProviderClient
    private var lastLatitude : Double = 0.0
    private var lastLongitud : Double = 0.0
    var PERMISSION_ID = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.current_weather_fragment, container, false)
        var nav = findNavController()

        mWeatherViewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        mFusedLocationProviderCliente = LocationServices.getFusedLocationProviderClient(requireContext())

        // Si hay internet, siempre va a obtener la ultima actualizacion de datos
        if(_Utils.isOnline(requireContext())) {
            updateCapitals()
            //insertLocationAndUpdateUI()
        }

        view.floatingActionButton.setOnClickListener {
            if(_Utils.isOnline(requireContext())) {
                updateCapitals()
                //insertLocationAndUpdateUI()
                Toast.makeText(requireContext(), "Actualizando datos..", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "No hay conexión a internet.. mostrando datos almacenados.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
        updateUI(null)
    }

    private fun insertLocationAndUpdateUI(){
        lastLatitude = 0.0
        lastLongitud = 0.0

        if(checkPermission()){
            if(isLocationEnabled()){
                mFusedLocationProviderCliente.lastLocation.addOnCompleteListener { task ->
                        var location = task.result
                        if(location == null){
                            getNewLocation()
                        }else{
                            lastLatitude = location.latitude
                            lastLongitud = location.longitude
                            // Insert database
                            updateDatabase()

                            // Update layout
                            updateUI(null)
                        }
                }
            }else{
                Toast.makeText(requireContext(), "Porfavor, encienda la ubicacion del celular", Toast.LENGTH_SHORT).show()
            }
        }else{
            requestPermission()
        }
    }

    private fun updateUI(mWeather: Weather?){

        var currentWeather: Weather? = mWeather
        if(currentWeather==null) {

                currentWeather = mWeatherViewModel.getWeatherLocal()

                if (currentWeather != null) {
                    try{
                    textView_condition.text = currentWeather!!.main
                    textView_description.text = currentWeather!!.description
                    textView_temperature.text = currentWeather!!.temp.toString() + "°C"
                    textView_feels_like_temperature.text =
                        currentWeather!!.name + ", " + currentWeather!!.country
                    textView_wind.text = "Wind: " + currentWeather!!.wind + " Km/h"
                    textView_precipitation.text =
                        "Humidity: " + currentWeather!!.humidity.toString() + " %"
                    textView_visibility.text =
                        "Cloudiness: " + currentWeather!!.clouds.toString() + " %"
                    textView_last_update.text =
                        "DateTime: " + _Utils.getDateTime(currentWeather!!.dt.toString())


                        var fileName : String = currentWeather!!.icon

                        if(!currentWeather!!.icon.contains("owm_"))
                            fileName = "owm_" + currentWeather!!.icon

                        var uri = "@drawable/" +fileName

                        var imageId : Int = resources.getIdentifier(uri, null, requireActivity().packageName)
                        if(imageId != 0){
                            var imageDraw : Drawable = resources.getDrawable(imageId)
                            imageView_condition_icon.setImageDrawable(imageDraw)
                        }
                    }catch(e: Exception){
                        Log.e("Error_image:","Ocurrió un error al buscar el recurso",)
                    }
                }

        }

    }

    private fun updateDatabase(){
        async {
            var newWeather: Weather
            if (lastLatitude != 0.0 && lastLongitud != 0.0)
                newWeather =
                    _Utils.weatherBuilder(_Utils.runRequestJson(lastLatitude, lastLongitud), true)
            else {
                // Ocurrió un error, por lo tanto, default location = "Montevideo"
                newWeather = _Utils.weatherBuilder(_Utils.runRequestJson("Montevideo"), true)
            }

            if (newWeather != null) {
                mWeatherViewModel.addWeather(newWeather)
                if(newWeather.timezone != 0)
                    _Utils.localTime = newWeather.timezone
            }
        }
    }

    private fun updateCapitals(){
        async {
            val mCapitals = _Utils.Companion.getAllCapitals()

            for ((i, latlon) in mCapitals) {
                var newWeather = _Utils.weatherBuilder(_Utils.runRequestJson(i), false)

                if (newWeather != null)
                    mWeatherViewModel.addWeather(newWeather)
            }

            insertLocationAndUpdateUI()
        }
    }

    private fun getNewLocation(){
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationProviderCliente!!.requestLocationUpdates(
            locationRequest,locationCallBack,Looper.myLooper()
        )
    }

    private val locationCallBack = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult){
            var lastLocation = p0.lastLocation

            lastLatitude = lastLocation.latitude
            lastLongitud = lastLocation.longitude
            // Insert database
            updateDatabase()
            // Update layout
            updateUI(null)
        }
    }
    private fun checkPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return false
        }
        return true
    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Tienes permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationEnabled(): Boolean{
        var locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}