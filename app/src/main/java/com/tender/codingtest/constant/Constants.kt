package com.tender.codingtest.constant

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {

    const val LOCATION = "location"
    const val FORECAST = "forecast"
    const val WEATHER_ENTITY = "weatherEntity"

    const val PREFERENCE_NAME = "WeatherAppPreferences"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"
    const val MAP_API_KEY = "AIzaSyBCnK6_6EOqA2RkDQhxQQZKy1sW0XDa9JY"
    const val WEATHER_API_KEY = "1900537d1a81f279c65aa36575229c44"
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/"
    const val METRIC_UNIT = "metric"

    fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            // deprecated.
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }

}