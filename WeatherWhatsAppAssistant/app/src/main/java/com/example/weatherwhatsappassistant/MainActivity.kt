//package com.example.weatherwhatsappassistant
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.location.LocationServices
//import retrofit2.*
//import retrofit2.converter.gson.GsonConverterFactory
//import java.net.URLEncoder
//import java.text.SimpleDateFormat
//import java.util.*
//import com.example.weatherwhatsappassistant.R
//import com.example.weatherwhatsappassistant.WeatherResponse
//import com.example.weatherwhatsappassistant.WeatherService
//
//class MainActivity : AppCompatActivity() {
//
//    val apiKey = "1ed04f264e035f843a83da5c5928742e"
//    val phoneNumber = "918874248693"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
//            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
//
//            requestPermissions(
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                1
//            )
//        }
//
//        val button = findViewById<Button>(R.id.sendBtn)
//
//        button.setOnClickListener {
//
////            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
////
////            if(hour in 0..23){
////                getLocation()
////            }else{
////                Toast.makeText(this,
////                    "It is before 10 AM or after 12 PM. WhatsApp cannot open.",
////                    Toast.LENGTH_LONG).show()
////            }
//            getLocation()
//
//        }
//    }
//
//    fun getLocation(){
//
//        val fusedLocationClient =
//            LocationServices.getFusedLocationProviderClient(this)
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//
//            if(location != null){
//                val lat = location.latitude
//                val lon = location.longitude
//                getTemperature(lat, lon)
//            }else{
//                Toast.makeText(this,"Location not found",Toast.LENGTH_LONG).show()
//            }
//
//        }
//
//    }
//
//    fun getTemperature(lat:Double, lon:Double){
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.openweathermap.org/data/2.5/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val service = retrofit.create(WeatherService::class.java)
//
//        service.getWeather(lat,lon,apiKey,"metric")
//            .enqueue(object: Callback<WeatherResponse>{
//
//                override fun onResponse(
//                    call: Call<WeatherResponse>,
//                    response: Response<WeatherResponse>) {
//
//                    val temp = response.body()?.main?.temp
//
//                    sendWhatsApp(temp.toString(),lat,lon)
//                }
//
//                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {}
//
//            })
//
//    }
//
//    fun sendWhatsApp(temp:String, lat:Double, lon:Double){
//
//        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
//        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
//
//        val mapLink = "https://maps.google.com/?q=$lat,$lon"
//
//        val message = """
//Temperature: $temp°C
//Date: $date
//Time: $time
//
//My Location:
//$mapLink
//""".trimIndent()
//
//        val url = "https://wa.me/$phoneNumber?text=" +
//                URLEncoder.encode(message,"UTF-8")
//
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse(url)
//
//        startActivity(intent)
//
//    }
//
//}

package com.example.weatherwhatsappassistant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.location.LocationServices
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val apiKey = "1ed04f264e035f843a83da5c5928742e"
    val phoneNumber = "918874248693"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun scheduleDailyWeather() {

            val request =
                androidx.work.PeriodicWorkRequestBuilder<WeatherWorker>(
                    1,
                    java.util.concurrent.TimeUnit.DAYS
                ).build()

            androidx.work.WorkManager
                .getInstance(this)
                .enqueue(request)
        }

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (hour < 10 || hour > 12) {

            Toast.makeText(
                this,
                "Its before 10 AM or after 12 PM. WhatsApp cannot open.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }
        startAutomation()


//        getLocation()
    }
//
//    fun getLocation() {
//
//        val fusedLocation =
//            LocationServices.getFusedLocationProviderClient(this)
//
//        fusedLocation.lastLocation.addOnSuccessListener { location ->
//
//            if (location != null) {
//
//                val lat = location.latitude
//                val lon = location.longitude
//
//                getWeather(lat, lon)
//            }
//        }
//    }

    fun startAutomation() {
        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getWeather(location.latitude, location.longitude)
            }
            }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            if(requestCode == 100){

                val result =
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                if(result?.get(0)?.contains("weather") == true){

                    startAutomation()
                }
            }
        }
        val mapFragment =
            supportFragmentManager
                .findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync { map ->

            val location = LatLng(lat,lon)

            map.addMarker(MarkerOptions().position(location))

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(location,15f)
            )
        }
    }
    fun getWeather(lat: Double, lon: Double) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)

        service.getWeather(lat, lon, apiKey)
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    val temp = response.body()?.main?.temp

                    val time =
                        SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(Date())

                    val date =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(Date())

                    val map =
                        "https://maps.google.com/?q=$lat,$lon"

                    val message =
                        "Temperature: $temp°C\nDate: $date\nTime: $time\nLocation: $map"

                    openWhatsApp(message)
                }

                override fun onFailure(
                    call: Call<WeatherResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MainActivity,
                        "Weather API failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun openWhatsApp(message: String) {

        val url =
            "https://wa.me/$phoneNumber?text=" + Uri.encode(message)

        val intent = Intent(Intent.ACTION_VIEW)

        intent.data = Uri.parse(url)

        startActivity(intent)

        finish()
    }
}

fun startVoiceCommand() {

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )

    startActivityForResult(intent, 100)
}
