package com.mobivone.sms.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class dataStored(val context: Context) {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")
        val Botton_State_Key =
            booleanPreferencesKey("Botton_State_Key") // Change to stringPreferencesKey

    }

    val Botton_ID_Flow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Botton_State_Key] ?: true
    }


    suspend fun storeUserData(bottonState: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Botton_State_Key] = bottonState
            Log.d("newValue","$bottonState")
        }
    }
}