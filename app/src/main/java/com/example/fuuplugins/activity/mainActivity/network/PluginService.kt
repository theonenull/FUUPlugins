package com.example.fuuplugins.activity.mainActivity.network

import com.example.fuuplugins.activity.mainActivity.network.bean.pluginListBean.PluginListBean
import com.example.fuuplugins.activity.mainActivity.network.bean.carousel.CarouselPicture
import com.example.fuuplugins.activity.mainActivity.network.bean.pluginItemBean.PluginItemBean
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming


interface PluginService {
    @GET("plugin/list")
    fun getPluginList(): Call<PluginListBean>

    @GET("plugin/{id}/md")
    @Streaming
    fun getPluginMd(@Path("id") id: String) : Call<ResponseBody>

    @GET("plugin/{id}/showJson")
    @Streaming
    fun getPluginJson(@Path("id") id: String) : Call<PluginItemBean>

    @GET("carousel/list")
    fun getCarouselPictureList() : Call<CarouselPicture>
}