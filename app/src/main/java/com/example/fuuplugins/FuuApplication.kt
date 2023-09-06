package com.example.fuuplugins

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.room.Room
import com.example.fuuplugins.plugin.PluginManager
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.plugin.Plugin
import com.example.fuuplugins.plugin.PluginConfig
import com.example.fuuplugins.plugin.PluginManager.Companion.loadJson
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
        lateinit var pluginsPathForApk : String
        lateinit var pluginsPathForWeb : String
        var isLoading = MutableStateFlow( false )
        val plugins = MutableStateFlow<List<Plugin>>(listOf())

        private val pluginsScope = CoroutineScope(Job())
        fun reloadPlugins(){
            pluginsScope.launch {
                plugins.emit(listOf())
                pluginInit()
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            this,
            FuuDatabase::class.java,
            "fuu"
        ).build()
        pluginsPathForApk = "${applicationContext.dataDir}/plugins_apk"
        pluginsPathForWeb = "${applicationContext.dataDir}/plugins_apk"
        val pluginsFile = File(pluginsPathForApk)
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
    FuuApplication.isLoading.value = true
    val dir = File(FuuApplication.pluginsPathForApk)
    val plugins  = mutableListOf<Plugin>()
    val fileFilter = FileFilter { file -> file.isDirectory && file.name.contains("plugin") }
    val files = dir.listFiles(fileFilter)
    if(files==null){
        FuuApplication.isLoading.value = false
        return
    }
    files.forEachIndexed{ index, file ->
        launch (Dispatchers.IO){
            val plugin = Plugin(
                iconPath = null,
                composeMethod = null,
                state = PluginState.SUCCESS,
                pluginObject = null
                ,
                pluginConfig = PluginConfig(
                    version = null,
                    minFuuVersion = null,
                    maxFuuVersion = null,
                    apkName = null,
                    developer = null
                )
            )
            val packageName = "com.example.testplugin"+"."
            try {
                PluginManager.loadPlugin(FuuApplication.instance, file)
                val composeProxyClassName = "ComposeProxy"
                val composeProxyClass = PluginManager.loadClass(packageName + composeProxyClassName)
                val getContent1Method: Method = composeProxyClass!!.getDeclaredMethod(
                    "MainCompose",
                    Repository::class.java,
                    Composer::class.java,
                    Int::class.java
                )
                val obj = composeProxyClass.newInstance()
                plugin.composeMethod = getContent1Method
                plugin.pluginObject = obj
            }catch (e:Exception){
                plugin.state = PluginState.ERROR
            }
            try {
                val iconFile = File(file.path,"icon.png")
                if(iconFile.exists()){
                    plugin.iconPath = iconFile.path.toString()
                }
                else{
                    throw Exception()
                }
            }catch (e:Exception) {
                plugin.state = PluginState.ERROR
            }
            try {
                val pluginConfigFile = File(file,"plugin.json")
                val config = loadJson(pluginConfigFile)
                plugin.pluginConfig = config
            }catch (e:Exception) {
                plugin.state = PluginState.ERROR
            }
            plugins.add(plugin)
            if(plugins.size == files.size){
                FuuApplication.isLoading.value = false
                FuuApplication.plugins.update { plugins }
            }
        }
    }
}