package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import com.example.fuuplugins.Base.BaseRepository
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.PluginService

import com.example.fuuplugins.activity.mainActivity.network.bean.carousel.CarouselPicture
import com.example.fuuplugins.activity.mainActivity.network.bean.pluginItemBean.PluginItemBean
import com.example.fuuplugins.activity.mainActivity.network.bean.pluginListBean.PluginListBean
import com.example.fuuplugins.network.OkHttpUtil
import com.example.fuuplugins.plugin.PluginConfig
import com.example.fuuplugins.plugin.PluginManager.Companion.loadJson
import com.example.fuuplugins.plugin.PluginManager.Companion.loadMarkDown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object PluginRepository : BaseRepository(){
    private var otherServiceInstance: PluginService? = null
    private val client: OkHttpClient by lazy {
        OkHttpUtil.getDefaultClient().newBuilder()
//            .hostnameVerifier { hostname, _ ->
//                if (DataManager.cloudSettings.trustAllCerts) true else hostname.endsWith(".fzu.edu.cn")
//            }
            .sslSocketFactory(OkHttpUtil.getSSLSocketFactory, OkHttpUtil.trustAllCerts[0])
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    private fun getPluginApi(): PluginService {
        if (otherServiceInstance == null) {
            val client = client.newBuilder().cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookies.forEach {
                        CookieUtil.jwchCookies[it.name] = it
                    }
                }
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    Log.d("RetrofitLoad", url.toString())
                    return CookieUtil.jwchCookies.values.filter {
                        it.matches(url)
                    }
                }
            }).protocols(listOf(Protocol.HTTP_1_1))
                .build()
            otherServiceInstance = createApi(test_server, client)
        }
        return otherServiceInstance!!
    }
    fun getPluginList(): Flow<PluginListBean> {
        return flow {
            val data = getPluginApi().getPluginList().execute().body() ?: throw Throwable("数据为空")
            emit(data)
        }
    }

    fun getPluginMd(id:String): Flow<String> {
        return flow {
            val data = getPluginApi().getPluginMd(id).execute().body() ?:throw Throwable("数据为空")
            val inputStream = data.byteStream()
            emit(loadMarkDown(inputStream))
        }.flowOn(Dispatchers.IO)
    }
    fun getPluginJson(id:String): Flow<PluginItemBean> {
        return flow {
            val data = getPluginApi().getPluginJson(id).execute().body() ?:throw Throwable("数据为空")
            emit(data)
        }.flowOn(Dispatchers.IO)
    }

    fun getCarouselPictureList(): Flow<CarouselPicture> {
        return flow {
            val data = getPluginApi().getCarouselPictureList().execute().body() ?:throw Throwable("数据为空")
            emit(data)
        }.flowOn(Dispatchers.IO)
    }

    fun downloadPlugin(id:String): Flow<Response<ResponseBody>> {
        return flow {
            val data = getPluginApi().downloadPlugin(id).execute() ?:throw Throwable("数据为空")
            emit(data)
        }.flowOn(Dispatchers.IO)
    }
}

val test_server ="http://10.133.47.154:8181/"