package com.example.weatherwhatsappassistant

import retrofit2.http.Query

@Dao
interface WeatherDao{

    @Insert
    fun insert(weather:WeatherEntity)

    @Query("SELECT * FROM WeatherEntity ORDER BY id DESC LIMIT 1")
    fun getLastWeather():WeatherEntity
}