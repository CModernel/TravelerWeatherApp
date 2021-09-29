package com.example.travelerweatherapp.Fragments.Map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.travelerweatherapp.Data.Weather
import com.example.travelerweatherapp.Fragments.WeatherCurrent.CurrentWeatherViewModel
import com.example.travelerweatherapp.R
import com.example.travelerweatherapp.Utils._Utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.current_weather_fragment.*
import org.jetbrains.anko.custom.async

class MapFragment : Fragment(), GoogleMap.OnMyLocationClickListener{

    private lateinit var map: GoogleMap
    private lateinit var mWeatherViewModel: MapViewModel

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        showCapitalsOnMap()
        map.setOnMyLocationClickListener(this)
        var uri = "@drawable/owm_01d"
        var imageId : Int = resources.getIdentifier(uri, null, requireActivity().packageName)
        var imageDraw : Drawable = resources.getDrawable(imageId)
        map.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(requireContext()))
        enableLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWeatherViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun showCapitalsOnMap(){
        createMarkers(mWeatherViewModel.getWeatherCapitals())
        var mCapitals = _Utils.Companion.getAllCapitals()
        // Id Mont - 3441575
        var montevideo = mCapitals.get(3441575)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(montevideo,9f),
            2000,
            null
        )
    }
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        } else{
            requestionLocationpermission()
        }
    }

    private fun requestionLocationpermission(){
        if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }else{
            Toast.makeText(requireContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    requireContext(),
                    "Para activar la localizacion ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(requireContext(), "Estás en ${p0.latitude} y ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    fun createMarkers(weatherList : List<Weather>){
        for(mWeather in weatherList){
            var fileName : String = mWeather!!.icon

            if(!mWeather.icon.contains("owm_"))
                fileName = "owm_" + mWeather!!.icon

            var uri = "@drawable/" +fileName
            var imageId : Int = resources.getIdentifier(uri, null, requireActivity().packageName)
            if(imageId == 0){
                // If imageId == 0, use default image
                uri = "@drawable/owm_01d"
                imageId = resources.getIdentifier(uri, null, requireActivity().packageName)
            }

            var imageDraw : Drawable = resources.getDrawable(imageId)
            map.addMarker(createMarker(mWeather))
                .tag=imageDraw
        }
    }

    fun createMarker(weather: Weather): MarkerOptions{
        var offset  = weather.timezone - _Utils.localTime

        var txtSnipper =  weather.main + ": " +weather.description +
                "\nTemp: " +weather.temp +" C°"+
                "\nHumidity: " +weather.humidity + "%" +
                "\nCloudiness: " +weather.clouds + "%" +
                "\nWind: "+ weather.wind + "Km/h" +
                "\nLocal Time: "+ _Utils.getDateTime((weather.dt + offset).toString())

        return MarkerOptions()
            .position(LatLng(weather.lat,weather.lon))
            .snippet(txtSnipper)
            .title(weather.name + ", " + weather.country)
    }

}