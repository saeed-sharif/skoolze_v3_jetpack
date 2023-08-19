package com.mobivone.sms.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MessageTable")
data class databaseModel (
    @PrimaryKey(autoGenerate = true)
    val id:Long?=null,
    val Recipient:String,
    val Number:String,
    val Message:String


        )

