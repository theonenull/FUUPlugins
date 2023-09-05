package com.example.fuuplugins.activity.composePluginActivity.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.ui.platform.ComposeView
import com.example.fuuplugins.activity.composePluginActivity.ActionForPlugin
import com.example.fuuplugins.activity.composePluginActivity.PluginManager
import com.example.fuuplugins.util.easyToast
import com.example.inject.repository.Repository
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.Array.newInstance
import java.lang.reflect.Field
import java.lang.reflect.Method

class PluginViewModel: ViewModel() {
    private val _isPluginLoadSuccess = MutableStateFlow(false)
    val isPluginLoadSuccess = _isPluginLoadSuccess.asStateFlow()

    private val _isMergeDexSuccess = MutableStateFlow(false)
    val isMergeDexSuccess = _isMergeDexSuccess.asStateFlow()

    var pluginActivityClass by mutableStateOf<Class<*>?>(null)
        private set
    var obj by mutableStateOf<Any?>(null)
    val composeProxyClassName = "com.example.testplugin.ComposeProxy"
    var pluginComposable1 by mutableStateOf<Method?>(null)
    var pluginComposable2 by mutableStateOf<@Composable (Repository) -> Unit>({})
    var isLoadPluginComposablesSuccess by mutableStateOf(false)

    fun mergeDex(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    PluginManager.loadPlugin(context)
                    if (PluginManager.pluginClassLoader != null) {
                        _isPluginLoadSuccess.value = true
                    }
                    if (PluginManager.mergeDexElement(context)) {
                        _isMergeDexSuccess.value = true
                    }
                    val composeProxyClass = PluginManager.loadClass(composeProxyClassName)
                    composeProxyClass?.let { proxyClass ->
                        val getContent1Method: Method = proxyClass.getDeclaredMethod(
                            "MainCompose",
                            Repository::class.java,
                            Composer::class.java,
                            Int::class.java
                        )
                        val obj2 = proxyClass.newInstance()
                        obj = obj2
                        pluginComposable1 = getContent1Method
                        isLoadPluginComposablesSuccess = true
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        easyToast("插件加载失败")
                    }
                }
            }
        }
    }
}

