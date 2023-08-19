package com.mobivone.sms.views.uicomponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mobivone.sms.R
import com.mobivone.sms.entities.databaseModel
import com.mobivone.sms.entities.smsDetail

@Composable
fun smsRow(smsDetail: databaseModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.wrapContentSize()) {
                Image(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(Modifier.wrapContentSize()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${smsDetail.Recipient}", color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${smsDetail.Number}", color = Color.Gray)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${smsDetail.Message}", color = Color.Gray)
                }

            }


        }

    }
}
