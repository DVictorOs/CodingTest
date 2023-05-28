package com.tender.codingtest.network

import com.tender.codingtest.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("2.5/forecast")
    fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") long : Double,
        @Query("units") units : String?,
        @Query("appid") appid : String?
    ) : Call<WeatherResponse>
}