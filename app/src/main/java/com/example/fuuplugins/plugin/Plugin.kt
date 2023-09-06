package com.example.fuuplugins.plugin

import java.lang.reflect.Method

data class Plugin(
    var iconPath: String?,
    var composeMethod: Method?,
    var state: PluginState,
    var pluginObject: Any?,
    var pluginConfig:PluginConfig,
    var markdown:String?
)

enum class PluginState{
    ERROR,
    LOADING,
    UNLOAD,
    SUCCESS
}

