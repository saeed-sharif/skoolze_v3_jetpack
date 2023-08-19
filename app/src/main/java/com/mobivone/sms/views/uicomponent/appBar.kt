package com.mobivone.sms.views.uicomponent

import android.widget.Toast
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.mobivone.sms.R
import com.mobivone.sms.database.AppDatabase
import com.mobivone.sms.utils.sendMessages
import kotlinx.coroutines.launch

@Composable
fun CostomappBar(
    title: String,
    infodailog: MutableState<Boolean>,


    ) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val sendMessages = sendMessages(context)
    TopAppBar(
        modifier = Modifier,
        title = { Text(text = "$title", color = Color.White) },
        actions = {
            IconButton(onClick = {
                infodailog.value = true

            }) {
                androidx.compose.material.Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = "",
                    tint = Color.White
                )

            }
            IconButton(onClick = {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                val db = AppDatabase.getInstance(context)
                coroutine.launch {
                    db.messageDao().deleteAllMessages()
                }


            }) {
                androidx.compose.material.Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "",
                    tint = Color.White
                )

            }
        },
        backgroundColor = colorResource(id = R.color.colorPrimary)

    )


}
