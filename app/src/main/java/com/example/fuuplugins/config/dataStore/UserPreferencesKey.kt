package com.example.fuuplugins.config.dataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fuuplugins.FuuApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferencesKey {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_NUMBER = stringPreferencesKey("user_number")
    val USER_MOOD = stringPreferencesKey("user_mood")
    val USER_USERNAME = stringPreferencesKey("user_username")
    val USER_PASSWORD = stringPreferencesKey("user_password")
    val USER_ACADEMY = stringPreferencesKey("user_academy")
    val USER_IS_LOGIN = booleanPreferencesKey("user_is_login")
    val USER_DATA_VALIDITY_PERIOD = stringPreferencesKey("user_data_validity_period")
}


fun <T1> getUserDataStore(
    key: Preferences.Key<T1>,
    defaultValue:T1
): Flow<T1> {
    return FuuApplication.instance.userDataStore.data.map {
            preferences -> preferences[key] ?: defaultValue
    }
}

suspend fun <T1> setUserDataStore(
    key: Preferences.Key<T1>,
    newValue:T1
) {
    FuuApplication.instance.userDataStore.edit {
            preferences -> preferences[key] = newValue
    }
}
