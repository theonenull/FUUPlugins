package com.example.fuuplugins.config.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.config.dataStore.DataStore.DATA_PREFERENCES_NAME
import com.example.fuuplugins.config.dataStore.DataStore.USER_PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStore {
    const val USER_PREFERENCES_NAME = "USER_DATA"
    const val DATA_PREFERENCES_NAME = "DATA_MANAGER"
    const val SETTING_PREFERENCES_NAME = "SETTING"
}

val Context.userDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

val Context.dataManageDataStore by preferencesDataStore(
    name = DATA_PREFERENCES_NAME
)

val Context.settingDataStore by preferencesDataStore(
    name = DATA_PREFERENCES_NAME
)

