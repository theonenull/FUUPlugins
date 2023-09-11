package com.example.fuuplugins.activity.mainActivity.network.bean.pluginListBean

import com.example.fuuplugins.activity.mainActivity.network.bean.pluginItemBean.PluginItemBean

data class PluginListBean(
    val code: String,
    val pluginList: List<PluginItemBean>
)