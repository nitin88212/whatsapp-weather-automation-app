package com.example.weatherwhatsappassistant
@Entity
data class WeatherEntity(

    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,

    val temp:String,
    val date:String,
    val time:String
)()
