package com.example.weatherwhatsappassistant

data class WeatherResponse(
    val main: Main
)

data class Main(
    val temp: Double
)