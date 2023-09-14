package com.example.fuuplugins

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composer
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.room.Room
import com.example.fuuplugins.activity.mainActivity.data.dao.FuuDatabase
import com.example.fuuplugins.activity.mainActivity.network.PluginService
import com.example.fuuplugins.config.dataStore.UserPreferencesKey
import com.example.fuuplugins.config.dataStore.setUserDataStore
import com.example.fuuplugins.plugin.Plugin
import com.example.fuuplugins.plugin.PluginConfig
import com.example.fuuplugins.plugin.PluginManager
import com.example.fuuplugins.plugin.PluginManager.Companion.loadJson
import com.example.fuuplugins.plugin.PluginManager.Companion.loadMarkDown
import com.example.fuuplugins.plugin.PluginState
import com.example.fuuplugins.service.PluginDownloadService
import com.example.fuuplugins.util.normalToast
import com.example.fuuplugins.util.toastInSuspend
import com.example.fuuplugins.weight.ExamWeight
import com.example.inject.repository.Repository
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter
import java.lang.reflect.Method


class FuuApplication: Application() {
    companion object{
        private val pluginsScope = CoroutineScope(Job())
        private val glancesScope = CoroutineScope(Job())
        lateinit var instance : Application
        lateinit var db : FuuDatabase
        lateinit var pluginsPathForApk : String
        lateinit var pluginsPathForWeb : String
        val isApkPluginsLoading = MutableStateFlow( false )
        val isWebPluginsLoading = MutableStateFlow( false )
        var isLoading = isApkPluginsLoading
            .zip(isWebPluginsLoading){ isApkPluginsLoading,isWebPluginsLoading->
                !isApkPluginsLoading && !isWebPluginsLoading
            }.stateIn(
                pluginsScope,
                started = SharingStarted.Eagerly,
                false
            )
        val apkPlugins = MutableStateFlow<List<Plugin.ApkPlugin>>(listOf())
        val webPlugins = MutableStateFlow<List<Plugin.WebPlugin>>(listOf())
        fun reloadPlugins(msg:String?){
            pluginsScope.launch {
                apkPlugins.emit(listOf())
                webPlugins.emit(listOf())
                pluginInit(msg)
            }
        }

        fun unInstallPlugin(id:String, callBack:suspend ()->Unit = {}){
            pluginsScope.launch (Dispatchers.IO){
                File(pluginsPathForApk,"plugin_${id}").let {
                    if(it.exists()){
                        it.deleteRecursively()
                    }
                }
                File(pluginsPathForWeb,"plugin_${id}").let {
                    if(it.exists()){
                        it.deleteRecursively()
                    }
                }
                reloadPlugins(msg = "重载插件")
                callBack.invoke()
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
        pluginsPathForWeb = "${applicationContext.dataDir}/plugins_web"
        pluginDirMake()
        pluginsScope.pluginInit(null)
        QbSdk.initX5Environment(this, object : PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(b: Boolean) {
            }
        })
        glancesScope.launch(Dispatchers.Default) {
            db.examDao().getAll().collect{
                val intent = Intent( instance, MyBroadcastReceiver::class.java)
                instance.sendBroadcast(intent)
            }
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
        val intent = Intent(applicationContext,PluginDownloadService::class.java)
        this.applicationContext.stopService(intent)
    }

    private fun pluginDirMake(){
        val apkPluginsFile = File(pluginsPathForApk)
        if(!apkPluginsFile.exists() && apkPluginsFile.parentFile?.exists() == true) {
            apkPluginsFile.mkdir()
        }
        val webPluginsFile = File(pluginsPathForWeb)
        if(!webPluginsFile.exists() && webPluginsFile.parentFile?.exists() == true) {
            webPluginsFile.mkdir()
        }
    }
}

fun CoroutineScope.pluginInit(msg:String?){
    launch (Dispatchers.IO){
        FuuApplication.isApkPluginsLoading.value = true
        FuuApplication.isWebPluginsLoading.value = true
        msg?.let{
            toastInSuspend(it)
        }
        loadApkPlugin()
        loadWebPlugin()
    }
}

private fun CoroutineScope.loadApkPlugin(){
    val dir = File(FuuApplication.pluginsPathForApk)
    val apkPlugins  = mutableListOf<Plugin.ApkPlugin>()
    val fileFilter = FileFilter { file -> file.isDirectory && file.name.contains("plugin") }
    val files = dir.listFiles(fileFilter)
    if(files==null){
        FuuApplication.isApkPluginsLoading.value = true
        return
    }
    files.forEachIndexed{ index, file ->
        launch (Dispatchers.IO){
            val plugin = Plugin.ApkPlugin(
                iconPath = null,
                composeMethod = null,
                state = PluginState.SUCCESS,
                pluginObject = null,
                pluginConfig = PluginConfig.ApkPluginConfig(
                    version = null,
                    minFuuVersion = null,
                    maxFuuVersion = null,
                    pluginName = null,
                    developer = null,
                    id = null
                ),
                markdown = null,
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
                Log.e("FuuApplication","loadApkPlugin: ${e.message}")
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
                Log.e("FuuApplication","loadApkPlugin: ${e.message}")
                plugin.state = PluginState.ERROR
            }
            try {
                val pluginConfigFile = File(file,"plugin.json")
                val config = loadJson<PluginConfig.ApkPluginConfig>(pluginConfigFile)
                plugin.pluginConfig = config
            }catch (e:Exception) {
                Log.e("FuuApplication","loadApkPlugin: ${e.message}")
                plugin.state = PluginState.ERROR
            }
            try {
                val markdownFile = File(file,"plugin.md")
                val markdown = loadMarkDown(markdownFile)
                plugin.markdown = markdown
            }catch (e:Exception) {
                Log.e("FuuApplication","loadApkPlugin: ${e.message}")
                plugin.state = PluginState.ERROR
            }
            apkPlugins.add(plugin)
            if(apkPlugins.size == files.size){
                FuuApplication.isApkPluginsLoading.value = true
                FuuApplication.apkPlugins.update { apkPlugins }
            }
        }
    }
}

private fun CoroutineScope.loadWebPlugin(){
    val dir = File(FuuApplication.pluginsPathForWeb)
    val webPlugins  = mutableListOf<Plugin.WebPlugin>()
    val fileFilter = FileFilter { file -> file.isDirectory && file.name.contains("plugin") }
    val files = dir.listFiles(fileFilter)
    if(files==null){
        FuuApplication.isApkPluginsLoading.value = true
        return
    }
    files.forEachIndexed{ _, file ->
        launch (Dispatchers.IO){
            val plugin = Plugin.WebPlugin(
                iconPath = null,
                url = null,
                state = PluginState.SUCCESS,
                pluginObject = null,
                pluginConfig = PluginConfig.WebPluginConfig(
                    version = null,
                    minFuuVersion = null,
                    maxFuuVersion = null,
                    pluginName = null,
                    developer = null,
                    url = ""
                ),
                markdown = null
            )
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
                val config = loadJson<PluginConfig.WebPluginConfig>(pluginConfigFile)
                plugin.url = config.url
                plugin.pluginConfig = config
            }catch (e:Exception) {
                plugin.state = PluginState.ERROR
            }
            try {
                val markdownFile = File(file,"plugin.md")
                val markdown = loadMarkDown(markdownFile)
                plugin.markdown = markdown
            }catch (e:Exception) {
                plugin.state = PluginState.ERROR
            }
            webPlugins.add(plugin)
            if(webPlugins.size == files.size){
                FuuApplication.isApkPluginsLoading.value = true
                FuuApplication.webPlugins.update { webPlugins }
            }
        }
    }
}

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("onReceive")
        val scope = CoroutineScope(Job())
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            scope.launch {
                context?.let {
                    val manager = GlanceAppWidgetManager(context)
                    val widget = ExamWeight()
                    val glanceIds = manager.getGlanceIds(widget.javaClass)
                    glanceIds.forEach { glanceId ->
                        widget.update(context, glanceId)
                    }
                }
            }
        } else {
            scope.launch {
                context?.let {
                    val manager = GlanceAppWidgetManager(context)
                    val widget = ExamWeight()
                    val glanceIds = manager.getGlanceIds(widget.javaClass)
                    glanceIds.forEach { glanceId ->
                        widget.update(context, glanceId)
                    }
                }
            }
        }
    }
}

