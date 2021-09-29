package com.example.travelerweatherapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.travelerweatherapp.Fragments.Register.RegisterViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController
    private lateinit var viewModel: RegisterViewModel
    var PERMISSION_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_nav.setupWithNavController(navController)
        requestLocation()

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        async {
            if (existeUser()) {
                navController.navigate(R.id.currentWeatherFragment)
            } else {
                navController.navigate(R.id.registerFragment)
            }
        }
    }

    fun existeUser(): Boolean{
        var retorno : Int = 0
         retorno =  viewModel.getCountUsers()
        return retorno > 0
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    private fun requestLocation(){
        if(!checkPermission()){
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this.baseContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.baseContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return false
        }
        return true
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }
}