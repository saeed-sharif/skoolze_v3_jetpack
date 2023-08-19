package com.mobivone.sms.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/*import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager*/


class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "My Service",
                "runningNotifcation",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }

      // setupWorker()
    }


  /*  private fun setupWorker() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<smsWorker>()
            .setInitialDelay(10, TimeUnit.MINUTES).setConstraints(constraints) // Set an initial delay of 10 minutes
            .build()



   *//*     val workRequest = PeriodicWorkRequestBuilder<smsWorker>(10, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()*//*

        WorkManager.getInstance(this).enqueue(workRequest)*/


    }
