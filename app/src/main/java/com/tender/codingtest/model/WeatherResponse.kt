package com.tender.codingtest.model

import java.io.Serializable

data class WeatherResponse(
    val cod : String,
    val message : Int,
    val cnt : Int,
    val list : ArrayList<Forecast>,
    val city : City
) : Serializable

data class Forecast (
    val dt : Long,
    val main : Main,
    val weather : ArrayList<Weather>,
    val clouds : Clouds,
    val wind : Wind,
    val visibility : Int,
    val pop : Double,
    val sys : Sys,
    val dt_txt : String
        ) : Serializable

data class Main (
    val temp : Double,
    val feels_like : Double,
    val temp_min : Double,
    val temp_max : Double,
    val pressure : Int,
    val sea_level :Int,
    val grnd_level : Int,
    val humidity : Int,
    val temp_kf : Double
        ) : Serializable

data class Weather (
    val id : Int,
    val main : String,
    val description : String,
    val icon : String
        ) : Serializable

data class Clouds (
    val all : Int
        ) : Serializable

data class Wind (
    val speed : Double,
    val deg : Int,
    val gust : Double
        ) : Serializable

data class Sys (
    val pod : String
        ) : Serializable

data class City (
    val id : Int,
    val name : String,
    val coord: Coord,
    val country : String,
    val population : Int,
    val timezone : Int,
    val sunrise : Long,
    val sunset : Long
        ) : Serializable

data class Coord (
    val lat : Double,
    val lon : Double
        ) : Serializable
