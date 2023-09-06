package com.example.fuuplugins.plugin

import java.lang.reflect.Method

data class Plugin(
    var iconPath: String?,
    var composeMethod: Method?,
    var state: PluginState,
    var pluginObject: Any?,
    var pluginConfig:PluginConfig
){
    override fun equals(other: Any?): Boolean {
        return this.composeMethod == this.composeMethod
    }

    override fun hashCode(): Int {
        var result = iconPath?.hashCode() ?: 0
        result = 31 * result + (composeMethod?.hashCode() ?: 0)
        result = 31 * result + state.hashCode()
        return result
    }
}

enum class PluginState{
    ERROR,
    LOADING,
    UNLOAD,
    SUCCESS
}

