package com.example.fuuplugins.plugin

import java.lang.reflect.Method


interface Plugin{
    var iconPath : String?
    var state: PluginState
    var markdown:String?
    var pluginConfig:PluginConfig
    data class ApkPlugin(
        override var iconPath: String?,
        override var state: PluginState,
        var composeMethod: Method?,
        var pluginObject: Any?,
        override var pluginConfig:PluginConfig,
        override var markdown:String?,
    ):Plugin
    data class WebPlugin(
        override var iconPath: String?,
        override var state: PluginState,
        var url: String?,
        var pluginObject: Any?,
        override var pluginConfig:PluginConfig,
        override var markdown:String?,
    ):Plugin
}

enum class PluginState{
    ERROR,
    LOADING,
    UNLOAD,
    SUCCESS
}

