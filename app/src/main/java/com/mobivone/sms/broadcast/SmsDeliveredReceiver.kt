package com.mobivone.sms.broadcast
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SmsDeliveredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show()
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
