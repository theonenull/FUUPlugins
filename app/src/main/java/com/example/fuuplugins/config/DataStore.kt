package com.example.fuuplugins.config

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.fuuplugins.config.dataStore.DataStore.USER_PREFERENCES_NAME

private val Context.UserDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)