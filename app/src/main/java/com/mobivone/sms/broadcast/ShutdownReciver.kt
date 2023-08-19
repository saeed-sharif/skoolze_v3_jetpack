package com.mobivone.sms.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mobivone.sms.utils.dataStored
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SHUTDOWN) {
           val datastored=dataStored(context!!)
            val scope= CoroutineScope(Dispatchers.IO)
            scope.launch { datastored.storeUserData(true) }


    }
}}