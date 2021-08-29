package com.example.weatherappmvvm.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappmvvm.repository.Repository
import com.example.weatherappmvvm.utils.Event
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val repository: Repository) : ViewModel() {

    val tvInputCity = MutableLiveData<String>()
    val etSearchLocation = MutableLiveData<String>()
    val inputTempC = MutableLiveData<String>()
    val inputTempF = MutableLiveData<String>()
    val inputLat = MutableLiveData<String>()
    val inputLon = MutableLiveData<String>()
    var visibility = MutableLiveData<Int>()
    var isResultSuccess = MutableLiveData<Boolean>()
    var weatherStatus = MutableLiveData<String>()

    init {
        visibility.value = View.GONE
    }

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    private val weatherData = MutableLiveData<String>()
    val weatherImage: LiveData<String>
        get() = weatherData

    fun getWeatherResponse(keyId: String, city: String, api: String) {
        viewModelScope.launch(IO) {
            repository.getWeatherData(keyId, city, api).let {
                if (it.isSuccessful) {
                    if (it.body() != null) {
                        withContext(Main) {
                            weatherData.postValue(it.body()!!.current.condition.icon)
                            tvInputCity.value = it.body()!!.location.name
                            inputTempC.value = "${it.body()!!.current.temp_c}°C"
                            inputTempF.value = "${it.body()!!.current.temp_c}°F"
                            inputLat.value = "${it.body()!!.location.lat}ϕ"
                            inputLon.value = "${it.body()!!.location.lon}λ"
                            weatherStatus.value = it.body()!!.current.condition.text
                            etSearchLocation.value = ""
                            isResultSuccess.value = true
                            visibility.value = View.VISIBLE
                        }
                    } else {
                        withContext(Main) {
                            visibility.value = View.GONE
                        }
                        isResultSuccess.value = false
                    }
                } else if (it.body() == null) {
                    withContext(Main) {
                        isResultSuccess.value = false
                    }
                    Log.e("TAG", "getWeatherResponseError: ${it.body()}")
                }
            }
        }
    }
}