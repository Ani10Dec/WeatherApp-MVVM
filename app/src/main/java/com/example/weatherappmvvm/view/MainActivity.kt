package com.example.weatherappmvvm.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappmvvm.R
import com.example.weatherappmvvm.databinding.ActivityMainBinding
import com.example.weatherappmvvm.repository.Repository
import com.example.weatherappmvvm.repository.database.UserDao
import com.example.weatherappmvvm.repository.database.UserDataBase
import com.example.weatherappmvvm.repository.retrofit.ApiServices
import com.example.weatherappmvvm.repository.retrofit.RetrofitInstances
import com.example.weatherappmvvm.utils.Apphelper
import com.example.weatherappmvvm.utils.Constants
import com.example.weatherappmvvm.viewmodel.MainViewModel
import com.example.weatherappmvvm.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    // Global Variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var apiServices: ApiServices
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Api Service Instance
        apiServices =
            RetrofitInstances.getWeatherRetroInstance().create(ApiServices::class.java)

        // Initiate userDao
        userDao = UserDataBase.getDatabaseInstance(this).getUserDao

        // Setting ViewModel
        val repo = Repository(apiServices, userDao)
        val factory = ViewModelFactory(repo, "main")
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        // Connecting XML ViewModel to ViewModel Class
        binding.viewModel = viewModel

        // Defining Activity Lifecycle
        binding.lifecycleOwner = this

        // Adding user name
        binding.userName.text = "Hi, ${intent.getStringExtra("name").toString()}"

        // Any Toast Message
        viewModel.message.observe(this, {
            it.getContentIfNotHandledOrReturnNull()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.weatherImage.observe(this, {
            Picasso.get().load("https:$it").error(R.drawable.ic_launcher_foreground)
                .into(binding.weatherImage)
        })

        // Checking for valid response
        viewModel.isResultSuccess.observe(this, {
            if (!it) {
                binding.llResponse.visibility = View.GONE
                Toast.makeText(this, "No matching location found", Toast.LENGTH_LONG).show()
            }
            Apphelper.hideProgressBar()
            Apphelper.hideKeyboard(this)
        })

        // Searching Weather
        binding.btnSearch.setOnClickListener {
            if (Apphelper.isOnline(this)) {
                if (checkForValidation()) {
                    Apphelper.showProgressBar(this)
                    viewModel.getWeatherResponse(
                        Constants.KEY,
                        binding.etSearchLocation.text.toString(),
                        Constants.API
                    )
                }
            } else {
                Apphelper.hideKeyboard(this)
                Toast.makeText(this, "No Internet!\nCheck Internet Connection", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun checkForValidation(): Boolean {
        if (binding.etSearchLocation.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter city name to proceed", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private var backPressed = false

    override fun onBackPressed() {

        if (backPressed) {
            super.onBackPressed()
            return
        }
        backPressed = true
        Toast.makeText(this, "Please click the back button twice to exit", Toast.LENGTH_SHORT)
            .show()
        Handler().postDelayed({ backPressed = false }, 2000)
    }
}