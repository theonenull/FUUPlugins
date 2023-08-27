package com.example.fuuplugins.config.dataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fuuplugins.FuuApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataManagePreferencesKey {
    //2023
    val DATA_MANAGE_CURRENT_YEAR = stringPreferencesKey("data_manage_current_year")
    //01
    val DATA_MANAGE_CURRENT_XUENIAN = stringPreferencesKey("data_manage_current_xuenian")
    //1
    val DATA_MANAGE_CURRENT_WEEK = stringPreferencesKey("data_manage_current_week")

    val DATA_MANAGE_CURRENT_ACADEMIC_YEAR = stringPreferencesKey("data_manage_current_academic_year")
}

fun <T1> getDataManageDataStore(
    key: Preferences.Key<T1>,
    defaultValue:T1
): Flow<T1> {
    return FuuApplication.instance.dataManageDataStore.data.map {
            preferences -> preferences[key] ?: defaultValue
    }
}

suspend fun <T1> setDataManageDataStore(
    key: Preferences.Key<T1>,
    newValue:T1
) {
    FuuApplication.instance.dataManageDataStore.edit {
        preferences -> preferences[key] = newValue
    }
}

suspend fun setYearWeek(year:String,week:String,xuenian:String){
    setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_YEAR,year)
    setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_XUENIAN,xuenian)
    setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_WEEK,week)
    setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_ACADEMIC_YEAR,"${year}0$xuenian")
}
