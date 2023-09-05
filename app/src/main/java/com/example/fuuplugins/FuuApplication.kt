package com.example.fuuplugins

import android.app.Application
import androidx.room.Room
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class FuuApplication: Application() {
    companion object{
        lateinit var instance : Application
        lateinit var db : FuuDatabase
        lateinit var pluginsPath : String
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            this,
            FuuDatabase::class.java,
            "fuu"
        ).build()
        pluginsPath = "${applicationContext.dataDir}/plugins"
        val pluginsFile = File(pluginsPath)
        if(!pluginsFile.exists() && pluginsFile.parentFile?.exists() == true) {
            pluginsFile.mkdir()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        db.close()
        val scope = CoroutineScope(Job())
        scope.launch(Dispatchers.IO) {
            setUserDataStore(
                UserPreferencesKey.USER_DATA_VALIDITY_PERIOD,
                "")
        }
    }
}