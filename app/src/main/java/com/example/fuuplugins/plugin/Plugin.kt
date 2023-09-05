package com.example.fuuplugins.plugin

import java.lang.reflect.Method

data class Plugin(
    val iconPath: String?,
    val composeMethod: Method?,
    val name:String,
    val state: PluginState,
    val pluginObject: Any?
){
    override fun equals(other: Any?): Boolean {
        return this.composeMethod == this.composeMethod
    }

    override fun hashCode(): Int {
        var result = iconPath?.hashCode() ?: 0
        result = 31 * result + (composeMethod?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
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

