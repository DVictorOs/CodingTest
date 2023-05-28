package com.tender.codingtest.dao

import androidx.room.*
import com.tender.codingtest.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insert(weatherEntity: WeatherEntity)

    @Delete
    suspend fun delete(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM 'weather-table'")
    fun fetchAllWeather() : Flow<List<WeatherEntity>>

}