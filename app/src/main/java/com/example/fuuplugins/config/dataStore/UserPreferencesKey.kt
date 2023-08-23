package com.example.fuuplugins.config.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKey {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_NUMBER = stringPreferencesKey("user_number")
    val USER_MOOD = stringPreferencesKey("user_mood")
    val USER_USERNAME = stringPreferencesKey("user_username")
    val USER_PASSWORD = stringPreferencesKey("user_password")
    val USER_ACADEMY = stringPreferencesKey("user_academy")
}