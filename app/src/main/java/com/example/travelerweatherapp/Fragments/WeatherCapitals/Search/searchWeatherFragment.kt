package com.example.travelerweatherapp.Fragments.WeatherCapitals.Search

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.travelerweatherapp.R
import com.example.travelerweatherapp.Utils._Utils
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.search_weather_fragment.*
import java.lang.Exception

class searchWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = searchWeatherFragment()
    }

    private lateinit var viewModel: SearchWeatherViewModel
    lateinit var placesClient : PlacesClient
    lateinit var placeId : String
    internal lateinit var view : View

    var placeFields = Arrays.asList(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =  inflater.inflate(R.layout.search_weather_fragment, container, false)
        initPlaces()
        setupPlacesAutocomplete()

        setupGetPhoto()
        return view
    }

    private fun setupGetPhoto() {
        var btnPhoto = view?.findViewById<Button>(R.id.btn_get_photo)
        btnPhoto.setOnClickListener {
            if(TextUtils.isEmpty(placeId)){
                Toast.makeText(requireContext(), "Elija una ubicación para la buscar la foto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getPhotoAndDetail(placeId)
        }
    }

    private fun getPhotoAndDetail(placeId: String) {
        val placeRequest = FetchPlaceRequest.builder(
            placeId!!,
            Arrays.asList(Place.Field.PHOTO_METADATAS,
            Place.Field.LAT_LNG)).build()
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { fetchPlaceResponse ->
                val place = fetchPlaceResponse.place

                // Get Photo
                val photoMetadata = place.photoMetadatas!![0]

                // Create request
                val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        image_view.setImageBitmap(bitmap)
                    }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchWeatherViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setupPlacesAutocomplete() {
        val autocompleteFragment = childFragmentManager.fragments[0] as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                placeId = p0.id.toString()

                var weather = _Utils.weatherBuilder(
                    _Utils.runRequestJson(
                        p0.latLng!!.latitude,
                        p0.latLng!!.longitude
                    ), false
                )
                Toast.makeText(requireContext(), "" + weather.description, Toast.LENGTH_SHORT).show()

                textView_condition2.text = weather!!.main
                textView_description2.text = weather!!.description
                textView_temperature2.text = weather!!.temp.toString() + "°C"
                textView_wind2.text = "Wind: " + weather!!.wind + " Km/h"
                textView_precipitation2.text =
                    "Humidity: " + weather!!.humidity.toString() + " %"
                textView_visibility2.text =
                    "Cloudiness: " + weather!!.clouds.toString() + " %"

                textView_feels_like_temperature2.text = weather!!.name + ", " + weather!!.country

                try{
                    var fileName : String = weather!!.icon

                    if(!weather!!.icon.contains("owm_"))
                        fileName = "owm_" + weather!!.icon

                    var uri = "@drawable/" +fileName

                    var imageId : Int = resources.getIdentifier(uri, null, requireActivity().packageName)
                    if(imageId != 0){
                        var imageDraw : Drawable = resources.getDrawable(imageId)
                        var imageView_icon2 = view?.findViewById<ImageView>(R.id.imageView_condition_icon2)
                        imageView_icon2.setImageDrawable(imageDraw)
                    }

                    btn_get_photo.visibility = View.VISIBLE
                    image_view.setImageBitmap(null)

                }catch(e: Exception){
                    Log.e("Error_image:","Ocurrió un error al buscar el recurso",)
                }

                btn_get_photo.visibility = View.VISIBLE
                image_view.setImageBitmap(null)
                //Toast.makeText(requireContext(), "" + p0.address, Toast.LENGTH_SHORT).show()
            }

            override fun onError(p0: Status) {
                Toast.makeText(requireContext(), "" + p0.statusMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initPlaces(){
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
    }

}