package com.example.weatherappmvvm.repository

import com.example.weatherappmvvm.model.pincode.PinCode
import com.example.weatherappmvvm.model.weather.Weather
import com.example.weatherappmvvm.repository.database.UserDao
import com.example.weatherappmvvm.repository.database.UserEntity
import com.example.weatherappmvvm.repository.retrofit.ApiServices
import retrofit2.Response

class Repository(private val apiServices: ApiServices, private val dao: UserDao) {

    suspend fun getPinCodeData(pin: String): Response<PinCode> {
        return apiServices.getPinCodeData(pin)
    }

    suspend fun getWeatherData(keyId: String, city: String, api: String): Response<Weather> {
        return apiServices.getWeatherData(keyId, city, api)
    }

    suspend fun insertUser(userEntity: UserEntity) {
        dao.insertUser(userEntity)
    }

}