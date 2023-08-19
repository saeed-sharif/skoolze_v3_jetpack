/*
package com.mobivone.sms.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mobivone.sms.database.AppDatabase
import com.mobivone.sms.entities.databaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class smsWorker(private val context: Context, val params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val sendMessages = sendMessages(context)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            println("run")
            Log.d("saeedData", "workerCalled")
            if (sendMessages.isButtonSendingInProgress == false) {
                val database = AppDatabase.getInstance(context)
                val messageList: LiveData<List<databaseModel>> = database.messageDao().getAllMessages()
                val snapshotStateList = messageList
                sendMessages.sendmessage(snapshotStateList,context)

            } else {
                println("Message Empty ")
            }


        }

        return Result.success()
    }
}
*/
