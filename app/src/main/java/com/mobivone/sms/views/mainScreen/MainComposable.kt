package com.mobivone.sms.views.mainScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mobivone.sms.R
import com.mobivone.sms.database.AppDatabase
import com.mobivone.sms.utils.RunningServices
import com.mobivone.sms.utils.ServerSetup
import com.mobivone.sms.utils.dataStored
import com.mobivone.sms.views.uicomponent.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainComposable(navController: NavController) {
    val context = LocalContext.current
   val isServiceRunning = RunningServices.isServiceRunning.observeAsState(initial = false).value
    var ipAddress by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val dataStored = remember { dataStored(context) }
    val floatingButtonState = dataStored.Botton_ID_Flow.collectAsState(initial = null)
    val database = AppDatabase.getInstance(context)
    val viewModel: ServerSetup = ServerSetup.getInstance(context)
    val messages = viewModel.messagesFlow.collectAsState(initial = emptyList()).value
    val dbitem = viewModel.Itemindb.collectAsState(initial = 0).value

    val size = messages.size
    ipAddress = getLocalIpAddress(context)!!

    var port = 5060
    var enteredPort by remember { mutableStateOf(port) }
    val infoDailog = remember { mutableStateOf(false) }
    val serverstartdialog = remember {
        mutableStateOf(false)
    }
    val serverstopdailog = remember {
        mutableStateOf(false)
    }

    if (infoDailog.value) {
        Info(ip = ipAddress, port = enteredPort.toString(), dismiss = infoDailog)
    }
    if (serverstartdialog.value) {
        serverStartDailog(
            title = "SMS gateway",
            ip = "$ipAddress",
            port = enteredPort,
            onPortChange = { newPort -> enteredPort = newPort },
            context = context,
            floatingButtonState as MutableState<Boolean>,
            serverstartdialog,
        )
    }
    if (serverstopdailog.value) {
        ServerClosingDailog(context, floatingButtonState as MutableState<Boolean>, serverstopdailog)
    }
    Scaffold(
        modifier = Modifier,
        topBar = {
            CostomappBar(title = "Mobi SMS", infoDailog)
        },
        floatingActionButton = {
            when (isServiceRunning) {
                false -> startServerBotton(serverstartdialog)
                true -> closeServerBotton(serverstopdailog)
                else -> null
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.background))
            ) {
                Box(
                    modifier = Modifier
                        .height(45.dp)
                        .fillMaxWidth()
                        .background(Color.Black),
                    Alignment.Center

                ) {
                    Text(
                        "Connected Clients: ${viewModel.counter.value.toString()}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth(),

                    ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(4.dp), // Add some padding for spacing
                            elevation = 5.dp
                        ) {
                            Column(
                                Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Pending $size", fontSize = 20.sp)
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(4.dp), // Add some padding for spacing
                            elevation = 5.dp
                        ) {
                            Column(
                                Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Send ${dbitem.toString()}",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    LazyColumn {
                        items(messages) { message ->

                            val state = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        scope.launch {
                                            database.messageDao().deleteMessageById(message.id!!)
                                        }

                                    }
                                    true
                                }
                            )
                            SwipeToDismiss(
                                state = state, background = {
                                    val color = when (state.dismissDirection) {
                                        DismissDirection.StartToEnd -> Color.Transparent
                                        DismissDirection.EndToStart -> Color.Red
                                        null -> Color.Transparent
                                    }
                                    color
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                            .background(color = color)

                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "delete",
                                            tint = Color.Black,
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }

                                },
                                dismissContent = {
                                    smsRow(smsDetail = message)
                                },
                                directions = setOf(DismissDirection.EndToStart)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun startServerBotton(serverstartdailog: MutableState<Boolean>) {
    IconButton(
        onClick = {
            serverstartdailog.value = true
        },
        modifier = Modifier
            .background(colorResource(id = R.color.Green), shape = CircleShape)
            .size(55.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.stope),
            contentDescription = "",
            tint = Color.White,
        )

    }
}

@Composable
fun closeServerBotton(serverstopdailog: MutableState<Boolean>) {
    IconButton(
        onClick = {

            serverstopdailog.value = true
        },
        modifier = Modifier
            .background(colorResource(id = R.color.colorPrimary), shape = CircleShape)
            .size(55.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.play),
            contentDescription = "",
            tint = Color.White,
        )

    }
}

private fun getLocalIpAddress(context: Context): String? {
    try {
        val wifiManager: WifiManager = context?.getSystemService(WIFI_SERVICE) as WifiManager
        return ipToString(wifiManager.connectionInfo.ipAddress)
    } catch (ex: Exception) {
        Log.e("IP Address", ex.toString())
    }
    return null
}

private fun ipToString(i: Int): String {
    return (i and 0xFF).toString() + "." +
            (i shr 8 and 0xFF) + "." +
            (i shr 16 and 0xFF) + "." +
            (i shr 24 and 0xFF)

}
