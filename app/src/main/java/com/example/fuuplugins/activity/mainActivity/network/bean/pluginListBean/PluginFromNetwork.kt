package com.example.fuuplugins.activity.mainActivity.network.bean.pluginListBean

data class PluginFromNetwork(
    val version : String? = null,
    val minFuuVersion : String? = null,
    val maxFuuVersion : String? = null,
    val pluginName : String? = null,
    val developer : String? = null,
    val description: String? = null,
    val id : String,
    val type : String,
    val platform : String
)