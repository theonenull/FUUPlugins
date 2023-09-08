package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import com.example.fuuplugins.Base.BaseRepository
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.OthersApiService
import com.example.fuuplugins.activity.mainActivity.network.PluginService
import com.example.fuuplugins.activity.mainActivity.network.bean.PluginListFromNetwork
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
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
    fun getPluginList(): Flow<PluginListFromNetwork?> {
        return flow {
            val data = getPluginApi().getPluginList()
            val sss = data.execute()
            emit(sss.body())
        }
    }
}

val test_server ="http://10.0.2.2:8181"