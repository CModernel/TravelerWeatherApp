package com.example.travelerweatherapp.Fragments.Register

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.travelerweatherapp.Data.User
import com.example.travelerweatherapp.Fragments.WeatherCurrent.currentWeatherFragment
import com.example.travelerweatherapp.MainActivity
import com.example.travelerweatherapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.register_fragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel
    var formatDate = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.register_fragment, container, false)
        var btnRegistar = view.findViewById<Button>(R.id.btnRegister)

        requireActivity().bottom_nav.visibility = View.INVISIBLE

        var btnDatePick = view.findViewById<Button>(R.id.btn_date_pick)
        btnDatePick.setOnClickListener(View.OnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener {
                datePicker, i, i2, i3 ->

                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR,i)
                selectDate.set(Calendar.MONTH,i2)
                selectDate.set(Calendar.DAY_OF_MONTH,i3)

                if(selectDate.time.after(Calendar.getInstance().time) || selectDate.time.equals(Calendar.getInstance().time)) {
                    Toast.makeText(
                        requireContext(),
                        "La fecha de nacimiento debe ser en el pasado.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    val date: String = formatDate.format(selectDate.time)
                    tvDoB.text = date
                }

            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        })


        btnRegistar.setOnClickListener {

            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()
            val fechaNac = tvDoB.text.toString()
            if(nombre.length != 0 && email.length != 0 && fechaNac.length != 0) {
                if(dataValidation(nombre, email, fechaNac))
                    registerUser(view, nombre, email, fechaNac)
            }else
                Toast.makeText(context, "Complete todos los datos", Toast.LENGTH_SHORT).show()
        }

    return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun registerUser(view: View, nombre: String, email: String, fechaNac: String)
    {
        viewModel.addUser(User(0,nombre,email,fechaNac))
        requireActivity().bottom_nav.visibility = View.VISIBLE
        Navigation.findNavController(view).navigate(R.id.currentWeatherFragment)
    }

    fun dataValidation(nombre : String, email : String, fechaNac : String) : Boolean =
        nameValidation(nombre) &&
        emailValidation(email) &&
        fechaNacValidation(fechaNac)

    fun nameValidation(nombre : String) : Boolean {
        if (nombre.contains(Regex("[^A-Z a-z]"))) {
            Toast.makeText(requireContext(), "El nombre solo puede contener letras", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    fun emailValidation(email : String) : Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    fun fechaNacValidation(fechaNac : String) : Boolean {
        if(fechaNac==null || fechaNac.isEmpty())
            Toast.makeText(requireContext(), "Ingrese una fecha de nacimiento valida.", Toast.LENGTH_SHORT).show()
        return true
    }
}