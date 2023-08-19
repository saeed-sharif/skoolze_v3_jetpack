package com.mobivone.sms.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import com.mobivone.sms.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messageContent = intent.getStringExtra("message")
        val messageId = intent.getIntExtra("messageId", -1)
        when (resultCode) {
            Activity.RESULT_OK -> {
                Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show()
                /**** this code is working what is actually concept **/
                //Delete Message
                deleteMessageFromDatabase(context, messageId)

            }
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                context,
                "SMS generic failure",
                Toast.LENGTH_SHORT
            )
                .show()
            SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                context,
                "SMS no service",
                Toast.LENGTH_SHORT
            )
                .show()
            SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                context,
                "SMS null PDU",
                Toast.LENGTH_SHORT
            ).show()
            SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                context,
                "SMS radio off",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

private fun deleteMessageFromDatabase(context: Context, messageId: Int) {
    if (messageId == -1) {
        return
    }

    val dataBase = AppDatabase.getInstance(context)
    val messageDao = dataBase.messageDao()

    // Delete the message from the database using the message ID
    GlobalScope.launch {
        messageDao.deleteMessageById(messageId.toLong()) // Convert Int to Long
    }
}
