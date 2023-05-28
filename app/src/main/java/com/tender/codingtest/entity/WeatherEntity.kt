package com.tender.codingtest.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "weather-table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val weatherResponse: String = ""
) : Serializable
