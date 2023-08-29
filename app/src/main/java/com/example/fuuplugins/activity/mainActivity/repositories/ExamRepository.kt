package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import com.example.fuuplugins.Base.BaseRepository
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.data.bean.ExamBean
import com.example.fuuplugins.activity.mainActivity.network.ExamService
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import com.example.fuuplugins.util.flowIO
import com.example.fuuplugins.util.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.concurrent.TimeUnit


object ExamRepository : BaseRepository(){

    private var jwchExamServiceInstance: ExamService? = null
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
    private fun getExamApi(): ExamService {
        if (jwchExamServiceInstance == null) {
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
            jwchExamServiceInstance = ExamRepository.createApi(JWCH_BASE_URL, client)
        }
        return jwchExamServiceInstance!!
    }

    suspend fun getExamStateHTML(): Flow<String> {
        return flow {
            val data = getExamApi().getExamState(CookieUtil.id).string()
            emit(data)
        }.flowIO()
    }

    suspend fun getExamsViewStateMap(stateHTML:String): Flow<Map<String, String>> {
        return flow {
            if (stateHTML.contains("请先对任课教师进行测评")) {
                throw Throwable("pingyi")
            }
            val viewStateMap = parseExamStateHTML(stateHTML)
            emit(viewStateMap)
        }.flowIO()

    }

    suspend fun getExams(viewStateMap: Map<String, String>, xq: String): Flow<List<ExamBean>> {
        return flow<List<ExamBean>> {
            val result = getExamApi().getExams(
                CookieUtil.id,
                xq,
                viewStateMap["EVENTVALIDATION"] ?: "",
                viewStateMap["VIEWSTATE"] ?: ""
            ).string()
            emit(parseExamsHTML(result))
        }.flowIO()
    }

    fun parseExamsHTML(result: String): List<ExamBean> {
        val exams = ArrayList<ExamBean>()
        val document = Jsoup.parse(result)
        val examElements = document.select("table[id=ContentPlaceHolder1_DataList_xxk]")
            .select("tr[style=height:30px; border-bottom:1px solid gray; border-left:1px solid gray; vertical-align:middle;]")
        println("getExamInfo: examList:" + examElements.size)
        for (i in examElements.indices) {
            val element = examElements[i]
            val tds = element.select("td")
            val name = tds[0].text()
            val xuefen = tds[1].text()
            val teacher = tds[2].text()
            val address = tds[3].text()
            val zuohao = tds[4].text()
            if (address.isNotEmpty()) {

            }
            val exam = ExamBean(name, xuefen, teacher, address, zuohao)
            exams.add(exam)
        }
        println(exams)
        return exams
    }

    private fun parseExamStateHTML(result: String): Map<String, String> {
        val document = Jsoup.parse(result)
        //设置常用参数
        val VIEWSTATE = document.getElementById("__VIEWSTATE").attr("value")
        val EVENTVALIDATION = document.getElementById("__EVENTVALIDATION").attr("value")
        val params = HashMap<String, String>()
        params["VIEWSTATE"] = VIEWSTATE
        params["EVENTVALIDATION"] = EVENTVALIDATION
        return params
    }


    fun newParseExamStateHTML(result: String): List<ExamBean> {
        val exams  = ArrayList<ExamBean>()
        val document = Jsoup.parse(result)
        val elementsWithSpecificStyle: Elements =
            document.select("[style*='height: 30px;'][style*='border-bottom: 1px solid gray;'][style*='border-left: 1px solid gray;'][style*='vertical-align: middle;']")
        elementsWithSpecificStyle.forEach {
            val childElements = it.select("[style*='padding-right: 1px;'][style*='padding-left: 1px;'][style*='padding-bottom: 1px;'][style*='padding-top: 1px;'][style*='border-right: 1px solid gray;'][style*='border-bottom: 1px solid gray;'][style*='border-left: 1px solid gray;']")
            val name = childElements[0].text()
            val xuefen = childElements[1].text()
            val teacher = childElements[2].text()
            val address = childElements[3].text()
            val zuohao = childElements[4].text()
            val exam = ExamBean(name, xuefen, teacher, address, zuohao)
            exams.add(exam)
        }
        return exams.toList()
    }
}