package com.example.fuuplugins.activity.mainActivity.network

import com.example.fuuplugins.activity.mainActivity.network.bean.PluginListFromNetwork
import retrofit2.Call
import retrofit2.http.GET


interface PluginService {
    @GET("plugin/list")
    fun getPluginList(): Call<PluginListFromNetwork>
}