package com.mobivone.sms.views.uicomponent

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mobivone.sms.R
import com.mobivone.sms.utils.RunningServices
import com.mobivone.sms.utils.dataStored
import kotlinx.coroutines.launch



@Composable
fun serverStartDailog(
    title: String,
    ip: String,
    port: Int,
    onPortChange: (Int) -> Unit,
    context: Context,
    isfloatingbotton :MutableState<Boolean>,
    dismiss: MutableState<Boolean>,


    ) {

    Dialog(onDismissRequest = { dismiss.value = false }) {
        val scope = rememberCoroutineScope()
        val dataStored = dataStored(context)
        Card(Modifier.wrapContentSize()) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "SMS Gateway", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip }, // Corrected onValueChange parameter

                        readOnly = true,

                        label = { Text(text = "IP") },
                        modifier = Modifier, // You should provide an appropriate Modifier here

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            // Corrected 'color' to 'colors'
                            focusedLabelColor = colorResource(id = R.color.colorPrimary), // You can set your desired color here
                            focusedBorderColor = colorResource(id = R.color.colorPrimary),
                        )
                    )


                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = port.toString(), onValueChange = { newPort ->
                            val newPortValue = newPort.toIntOrNull() ?: port
                            onPortChange(newPortValue)

                        },
                        label = { Text(text = "Port") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.colorPrimary),
                            focusedLabelColor = colorResource(id = R.color.colorPrimary)
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 20.dp, 0.dp),
                    Arrangement.End
                ) {
                    TextButton(onClick = { dismiss.value = false }) {
                        Text(text = "CANCEL", color = colorResource(id = R.color.colorPrimary))
                    }
                    TextButton(onClick = {
                        val intent = Intent(context, RunningServices::class.java)
                        intent.putExtra("ip", ip)
                        intent.putExtra("port", port)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                        scope.launch { dataStored.storeUserData(false) }
                        isfloatingbotton.value=false
                        println("Server started")
                        dismiss.value = false
                        println("next Executed ")
                        scope.launch { }
                        Toast.makeText(context, "Server Started", Toast.LENGTH_SHORT)
                            .show()// Start the server using the startServer() method

                    }) {
                        Text(text = "START", color = colorResource(id = R.color.colorPrimary))
                    }
                }
            }
        }
    }
}


@Composable
fun ServerClosingDailog(
    context: Context,
    isfloatingbotton: MutableState<Boolean>,
    serverstopdailog: MutableState<Boolean>,

    ) {
    Dialog(onDismissRequest = { serverstopdailog.value = false }) {
        val dataStored = dataStored(context)
        val scope = rememberCoroutineScope()

        Card(modifier = Modifier.wrapContentSize()) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Stop Server", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Do you want to stop SMS gateway?")
                }
                Row(Modifier.fillMaxWidth(), Arrangement.End) {
                    TextButton(onClick = {
                        serverstopdailog.value = false
                    }) {

                        Text(text = "No", color = colorResource(id = R.color.colorPrimary))
                    }
                    TextButton(onClick = {
                        val intent = Intent(context, RunningServices::class.java)
                        context.stopService(intent)
                        scope.launch { dataStored.storeUserData(true) }
                        isfloatingbotton.value=true
                        serverstopdailog.value = false
                        Toast.makeText(context, "Server Stopped", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "Yes", color = colorResource(id = R.color.colorPrimary))

                    }
                }
            }

        }
    }
}

@Composable
fun Info(
    ip: String,
    port: String,
    dismiss: MutableState<Boolean>
) {
    Dialog(onDismissRequest = { dismiss.value = false }) {
        Card(Modifier.wrapContentSize()) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip }, // Corrected onValueChange parameter

                        readOnly = true,

                        label = { Text(text = "IP") },
                        modifier = Modifier, // You should provide an appropriate Modifier here

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            // Corrected 'color' to 'colors'
                            focusedLabelColor = colorResource(id = R.color.colorPrimary), // You can set your desired color here
                            focusedBorderColor = colorResource(id = R.color.colorPrimary),
                        )
                    )


                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = port, onValueChange = {
                        },
                        label = { Text(text = "Port") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.colorPrimary),
                            focusedLabelColor = colorResource(id = R.color.colorPrimary)
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 20.dp, 0.dp),
                    Arrangement.End
                ) {
                    TextButton(onClick = { dismiss.value = false }) {
                        Text(text = "OK", color = colorResource(id = R.color.colorPrimary))

                    }

                }
            }
        }
    }
}


@Composable
fun SendingDailog() {
    Dialog(
        onDismissRequest = {

        },

        ) {
        Card(modifier = Modifier.wrapContentSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    CircularProgressIndicator(color = colorResource(id = R.color.colorPrimary))
                }
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp),
                    Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Sending...")

                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    SendingDailog()
}