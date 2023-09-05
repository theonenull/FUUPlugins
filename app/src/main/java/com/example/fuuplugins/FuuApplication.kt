package com.example.fuuplugins

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.room.Room
import com.example.fuuplugins.activity.composePluginActivity.PluginManager
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.plugin.Plugin
import com.example.fuuplugins.plugin.PluginState
import com.example.inject.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter
import java.lang.reflect.Method

class FuuApplication: Application() {
    companion object{
        lateinit var instance : Application
        lateinit var db : FuuDatabase
        lateinit var pluginsPath : String
//        private var _plugins = mutableMapOf<>()
        val plugins = MutableStateFlow<List<Plugin>>(listOf())
        private val pluginsScope = CoroutineScope(Job())
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

        pluginsScope.pluginInit()

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

fun CoroutineScope.pluginInit(){
    val dir = File(FuuApplication.pluginsPath)
    val fileFilter = FileFilter { file -> file.isDirectory && file.name.contains("plugin") }
    val files = dir.listFiles(fileFilter)
    files?.forEachIndexed{ index, file ->
        launch (Dispatchers.IO){
            val packageName = "com.example.testplugin"+"."
            try {
                PluginManager.loadPlugin(FuuApplication.instance)
                val composeProxyClassName = "ComposeProxy"
                val composeProxyClass = PluginManager.loadClass(packageName+composeProxyClassName)
                composeProxyClass?.let { proxyClass ->
                    val getContent1Method: Method = proxyClass.getDeclaredMethod(
                        "MainCompose",
                        Repository::class.java,
                        Composer::class.java,
                        Int::class.java
                    )
                    val obj = proxyClass.newInstance()
                    FuuApplication.plugins.emit (
                        FuuApplication.plugins.value.toMutableList().plus(
                            Plugin(
                                "",
                                getContent1Method,
                                name = "test",
                                state = PluginState.SUCCESS,
                                pluginObject = obj
                            ))
                    )
                    println("yes")
                }
            }catch (e:Exception){
                println("wrong")
                FuuApplication.plugins.emit(
                    FuuApplication.plugins.value.toMutableList().plus(Plugin(null,null, name = "test", state = PluginState.ERROR, pluginObject = null))
                )
            }
        }
    }
}