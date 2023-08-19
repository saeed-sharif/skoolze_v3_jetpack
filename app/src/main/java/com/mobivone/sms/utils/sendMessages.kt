package com.mobivone.sms.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import com.mobivone.sms.broadcast.SmsDeliveredReceiver
import com.mobivone.sms.broadcast.SmsSentReceiver
import com.mobivone.sms.entities.databaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class sendMessages(private val context: Context) {
    fun sendMessage(databaseModelList: List<databaseModel>) {
        println("Sending messages in progress")
        val coroutine = CoroutineScope(Dispatchers.Default)
        for (message in databaseModelList) {
            coroutine.launch { sendSms(message) }
        }
        println("Sending messages completed")
    }
    private suspend fun sendSms(singleMessage: databaseModel): Boolean {
        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                val subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
                SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
            } else {
                SmsManager.getDefault()
            }

            val phoneNumber = singleMessage.Number?.replace("[\\s\\-()]".toRegex(), "") ?: ""
            val message = singleMessage.Message ?: ""

            val sentIntents = ArrayList<PendingIntent>()
            val deliveredIntents = ArrayList<PendingIntent>()

            val sentPI = PendingIntent.getBroadcast(
                context,
                singleMessage.id?.toInt() ?: 0, // Use a unique request code for each message
                Intent(context, SmsSentReceiver::class.java).apply {
                    putExtra("message", message)
                    putExtra(
                        "messageId",
                        singleMessage.id?.toInt() ?: 0
                    ) // Assuming you have a unique ID for each message
                },
                PendingIntent.FLAG_IMMUTABLE
            )

            for (i in message.indices) {
                sentIntents.add(sentPI)
                deliveredIntents.add(
                    PendingIntent.getBroadcast(
                        context,
                        i,
                        Intent(context, SmsDeliveredReceiver::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            val messageParts = arrayListOf(message)
            smsManager.sendMultipartTextMessage(
                phoneNumber,
                null,
                messageParts,
                sentIntents,
                deliveredIntents
            )

            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        } finally {
            println("One message sent and done")
        }
    }
}
