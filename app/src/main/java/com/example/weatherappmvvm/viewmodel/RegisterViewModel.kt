package com.example.weatherappmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappmvvm.model.pincode.PinCode
import com.example.weatherappmvvm.repository.Repository
import com.example.weatherappmvvm.repository.database.UserEntity
import com.example.weatherappmvvm.utils.Event
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    val inputNumber = MutableLiveData<String>()
    val inputName = MutableLiveData<String>()
    val inputGender = MutableLiveData<String>()
    val inputDOB = MutableLiveData<String>()
    val inputAge = MutableLiveData<String>()
    val inputAddress1 = MutableLiveData<String>()
    val inputAddress2 = MutableLiveData<String>()
    val inputPinCode = MutableLiveData<String>()
    val inputDistrict = MutableLiveData<String>()
    val inputState = MutableLiveData<String>()


    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage


    private val pinCodeData = MutableLiveData<PinCode>()
    val pinCodeResponse: LiveData<PinCode>
        get() = pinCodeData

    fun getPinCodeData() {
        viewModelScope.launch(IO) {
            repository.getPinCodeData(inputPinCode.value.toString()).let {
                if (it.isSuccessful) {
                    pinCodeData.postValue(it.body())
                } else {
                    Log.e("Response Error", "getPinCodeData: ${it.code()}")
                }
            }
        }
    }

    fun addUserToDatabase() {
        val user = UserEntity(
            0,
            inputNumber.value!!,
            inputName.value!!,
            inputGender.value!!,
            inputDOB.value!!,
            inputAge.value!!,
            inputAddress1.value!!,
            inputAddress2.value,
            inputPinCode.value!!,
            inputDistrict.value!!,
            inputState.value!!,
        )
        viewModelScope.launch(IO) {
            insertUser(user)
        }
    }

    private fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(IO) {
            repository.insertUser(userEntity)
        }
    }

}