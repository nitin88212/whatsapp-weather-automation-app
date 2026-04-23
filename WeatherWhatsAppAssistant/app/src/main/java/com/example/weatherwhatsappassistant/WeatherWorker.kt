package com.example.weatherwhatsappassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.*

class WeatherWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {

        val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
        val time = SimpleDateFormat("HH:mm").format(Date())

        val message =
            "Daily Weather Report\nDate: $date\nTime: $time"

        val url =
            "https://wa.me/918874248693?text=" + Uri.encode(message)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        applicationContext.startActivity(intent)

        return Result.success()
    }
}