package com.example.weatherappmvvm.repository.retrofit

import com.example.weatherappmvvm.model.pincode.PinCode
import com.example.weatherappmvvm.model.weather.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @GET("pincode/{pinId}")
    suspend fun getPinCodeData(@Path(value = "pinId") id: String): Response<PinCode>


    @GET("v1/current.json")
    suspend fun getWeatherData(
        @Query("key") keyId: String,
        @Query("q") city: String,
        @Query("api") apiNo: String
    ): Response<Weather>
}