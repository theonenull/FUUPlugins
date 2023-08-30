package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import com.example.fuuplugins.Base.BaseRepository
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.JwchLoginService
import com.example.fuuplugins.activity.mainActivity.network.OthersApiService
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.config.dataStore.DataManagePreferencesKey
import com.example.fuuplugins.config.dataStore.dataManageDataStore
import com.example.fuuplugins.config.dataStore.setDataManageDataStore
import com.example.fuuplugins.network.OkHttpUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.jsoup.Jsoup
import java.util.Calendar
import java.util.concurrent.TimeUnit

object OtherRepository:BaseRepository(){
    private var otherServiceInstance: OthersApiService? = null
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
    private fun getJwchApi(): OthersApiService {
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
            otherServiceInstance = createApi(JWCH_BASE_URL, client)
        }
        return otherServiceInstance!!
    }

    fun getSchoolCalendar(xq: String): Flow<String> {
        return flow {
            emit(String(CourseRepository.getOthersApi().getSchoolCalendar(xq).bytes(), charset("gb2312")) )
        }
    }

    /**
     * 解析开学时间网页
     * @param result 获取到的网页
     */
    suspend fun parseBeginDate(xq: String, result: String) {
            val document = Jsoup.parse(result)
            val select = document.getElementsByTag("select")[0]
            val option = select.getElementsByAttributeValueStarting("value", xq)
            if (option.size > 0) {
                val value = option[0].attr("value")
                val beginYear = value.substring(6, 10).toInt()
                val beginMonth = value.substring(10, 12).toInt()
                val beginDay = value.substring(12, 14).toInt()
                setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_DAY,beginDay)
                setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_MONTH,beginMonth)
                setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_START_YEAR,beginYear)
                println(beginMonth)
//                val calendar = Calendar.getInstance()
//                calendar.set(beginYear, beginMonth - 1, beginDay, 0, 0, 0)
                //存储当前设置的学期开学时间
//                DataManager.beginDate = calendar.timeInMillis
                //根据开学时间和当前时间计算出当前周数
//                val startMillis = calendar.timeInMillis
//                val endYear = value.substring(14, 18).toInt()
//                val endMonth = value.substring(18, 20).toInt()
//                val endDay = value.substring(20, 22).toInt()
//                calendar.set(endYear, endMonth - 1, endDay)
//                val endMillis = calendar.timeInMillis
//                val endWeek = getWeeks(startMillis, endMillis)
//                DataManager.endWeek = endWeek
            }


    }

}