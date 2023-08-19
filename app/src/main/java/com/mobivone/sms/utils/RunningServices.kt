package com.mobivone.sms.utils

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.mobivone.sms.R
import com.mobivone.sms.entities.databaseModel
import kotlinx.coroutines.*

class RunningServices : Service() {
    private val NOTIFICATION_ID = 1
    private lateinit var viewModel: ServerSetup
    private var isServerRunning = false // Flag to track if the server is running
    private val receivedMessages: MutableList<databaseModel> = mutableListOf()
    private lateinit var smsSender: sendMessages
    private var isSendingScheduled = false
    private var scheduledJob: Job? = null

    private val stopAction: NotificationCompat.Action by lazy {
        val stopIntent = Intent(this, RunningServices::class.java)
        stopIntent.action = ACTION_STOP_SERVICE
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        NotificationCompat.Action.Builder(
            R.drawable.stope, // Replace with your stop icon
            "Stop Service",
            stopPendingIntent
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        smsSender = sendMessages(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val scope = CoroutineScope(Dispatchers.Main)
        if (intent.action == ACTION_STOP_SERVICE) {
            stopService()
            return START_NOT_STICKY
        }
        Log.d("isServiceRunning","$isServerRunning")
        _isServiceRunning.value = true
        // Use Main dispatcher for UI-related work
        scope.launch {
            viewModel.messagesFlow.collect { messages ->
                receivedMessages.clear()
                receivedMessages.addAll(messages)
                // If no sending task is scheduled, schedule one
                if (!isSendingScheduled) {
                    isSendingScheduled = true

                    // Cancel any previous scheduled job
                    scheduledJob?.cancel()

                    // Schedule a new sending task with a delay
                    scheduledJob = launch {
                        delay(20000) // 20,000 milliseconds = 20 seconds

                        // Send the messages and reset the flag
                        smsSender.sendMessage(receivedMessages)
                        Log.d("livedata", receivedMessages.toString())
                        isSendingScheduled = false
                    }
                }
            }
        }
        val ip = intent.getStringExtra("ip")
        val port = intent.getIntExtra("port", 0)
        createNotificationChannel(ip!!, port)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Skoolze Sms Gateway")
            .setContentText("IP: $ip, Port: $port")
            .setSmallIcon(R.drawable.micon)
            .addAction(stopAction) // Add the stop action

        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)

        // Obtain the shared instance of the ViewModel
        viewModel = ServerSetup.getInstance(this)
        // Check if the server is already running
        if (!isServerRunning) {
            viewModel.startServer(ip = ip!!, port = port, context = this)
            isServerRunning = true
        }

        /* Log.d("livedata", messagesLiveData.value.toString())*/
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceRunning.value = false
        Log.d("isServiceRunning","$isServerRunning")
        val dataStored = dataStored(this)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            dataStored.storeUserData(true)
        }

        // Stop the server if it was running
        if (isServerRunning) {
            viewModel.stopServer()
            Log.d("isServiceRunning","$isServerRunning")
            isServerRunning = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel(ip: String, port: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val stopIntent = Intent(this, RunningServices::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Skoolze Sms Gateway")
                .setContentText("IP: $ip, Port: $port")
                .setSmallIcon(R.drawable.micon)
                .addAction(stopAction) // Add the stop action

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            manager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
        _isServiceRunning.value = false
        Log.d("isServiceRunning","$isServerRunning")

    }

    companion object {
        private val _isServiceRunning = MutableLiveData<Boolean>()
        val isServiceRunning: LiveData<Boolean> = _isServiceRunning
        private const val ACTION_STOP_SERVICE = "com.mobivone.sms.utils.STOP_SERVICE"
        private const val CHANNEL_ID = "ForegroundServiceChannel"
    }

}
