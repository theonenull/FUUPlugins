package com.example.fuuplugins.activity.mainActivity.network.bean.pluginItemBean

import com.example.fuuplugins.activity.mainActivity.network.bean.pluginListBean.PluginFromNetwork


data class PluginItemBean(
    val iconPath: String,
    val pluginConfig: PluginFromNetwork
)