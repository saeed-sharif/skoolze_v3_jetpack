package com.mobivone.sms.utils

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mobivone.sms.database.AppDatabase
import com.mobivone.sms.entities.ClientResponse
import com.mobivone.sms.entities.databaseModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex


class ServerSetup(private val context: Context) : ViewModel() {
    private val selectorManager = SelectorManager(Dispatchers.IO)
    private var serverSocket: ServerSocket? = null
    private val clientCountMutex = Mutex()
    private val databaseLock = Any()
    private var isRunning = false
    private val _counter = mutableStateOf(0)
    val counter: State<Int> = _counter

    val messagesFlow: Flow<List<databaseModel>> by lazy {
        AppDatabase.getInstance(context).messageDao().getAllMessages()
    }
    val Itemindb: Flow<Int> by lazy {
        AppDatabase.getInstance(context).messageDao().getCount()
    }

    fun startServer(ip: String, port: Int, context: Context) {
        serverSocket = aSocket(selectorManager).tcp().bind(ip, port)
        println("Server is listening at ${serverSocket?.localAddress}")

        isRunning = true

        viewModelScope.launch {
            while (serverSocket != null && !serverSocket!!.isClosed) {
                val socket = serverSocket!!.accept()
                println("Accepted $socket")
                _counter.value++
                launch(Dispatchers.IO) {
                    handleClient(socket, context)
                }
            }
        }
    }

    fun stopServer() {
        isRunning = false
        viewModelScope.cancel() // Cancel all ongoing coroutines
        serverSocket?.close()
        serverSocket = null
        _counter.value=0

    }

    private suspend fun handleClient(socket: Socket, context: Context) {
        val receiveChannel = socket.openReadChannel()
        try {
            while (true) {
                val name = receiveChannel.readUTF8Line()
                val gson = Gson()
                val responseData = gson.fromJson(name, ClientResponse::class.java)
                val insertData = databaseModel(
                    Recipient = responseData.Recipient,
                    Number = responseData.Number,
                    Message = responseData.Message
                )
                val dataBase = AppDatabase.getInstance(context)
                synchronized(databaseLock) {
                    dataBase.messageDao().insert(insertData)
                }
            }
        } catch (e: Throwable) {
            println("${e.message}")
            socket.close()
            _counter.value=0
        }
    }


    companion object {
        private var viewModelInstance: ServerSetup? = null

        // Initialize the instance with the provided context
        fun getInstance(context: Context): ServerSetup {
            if (viewModelInstance == null) {
                viewModelInstance = ServerSetup(context)
            }
            return viewModelInstance!!
        }
    }


    /*  companion object {
          private val viewModelInstance: ServerSetup = ServerSetup()

          fun getInstance(): ServerSetup {
              return viewModelInstance
          }
      }*/
}


