package com.example.fuuplugins.activity.mainActivity.network.bean

data class PluginListFromNetwork(
    val code: String,
    val pluginList: List<PluginOnServer>
)

data class PluginJsonFromNetwork(
    val code: String,
    val data: PluginOnServer
)