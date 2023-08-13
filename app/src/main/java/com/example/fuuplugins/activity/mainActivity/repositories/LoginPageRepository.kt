package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.UndergraduateApiService
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64
import java.util.concurrent.TimeUnit

interface LoginPageRepository{

}

object BlockLoginPageRepository : LoginPageRepository{
    private var jwchApiServiceInstance: UndergraduateApiService? = null
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
    fun getJwchApi(): UndergraduateApiService {
        if (jwchApiServiceInstance == null) {
            val client = client.newBuilder().cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    Log.d("RetrofitSave", url.toString())
                    cookies.forEach {
                        CookieUtil.jwchCookies[it.name] = it
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    Log.d("RetrofitLoad", url.toString())
                    return CookieUtil.jwchCookies.values.toList()
                }
            }).protocols(listOf(Protocol.HTTP_1_1))
                .build()
            jwchApiServiceInstance = createApi(JWCH_BASE_URL, client)
        }
        return jwchApiServiceInstance!!
    }
    suspend fun getVerifyCode(): Flow<ByteArray> {
        return flow {
            val inputString = getJwchApi().getVerifyCode().bytes()
            emit(
                inputString
            )
        }
    }

    private inline fun <reified T> createApi(url: String, client: OkHttpClient): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }
}