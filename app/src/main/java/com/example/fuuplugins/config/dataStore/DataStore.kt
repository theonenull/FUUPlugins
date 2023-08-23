package com.example.fuuplugins.config.dataStore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.fuuplugins.config.dataStore.DataStore.USER_PREFERENCES_NAME

object DataStore {
    const val USER_PREFERENCES_NAME = "USER_DATA"
}

val Context.userDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)