package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log

import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.data.course.CourseBean
import com.example.fuuplugins.activity.mainActivity.network.JwchCourseService
import com.example.fuuplugins.activity.mainActivity.network.JwchLoginService
import com.example.fuuplugins.activity.mainActivity.viewModel.ClassScheduleViewModel
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import com.example.fuuplugins.util.catchWithMassage
import com.example.fuuplugins.util.debug
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object ClassScheduleRepository {
    private var jwchCourseServiceInstance: JwchCourseService? = null
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
    fun getJwchCourseApi(): JwchCourseService {
        if (jwchCourseServiceInstance == null) {
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
            jwchCourseServiceInstance = createApi(JWCH_BASE_URL, client)
        }
        return jwchCourseServiceInstance!!
    }




    fun getCourseStateHTML(): Flow<String> {
        return flow {
            val data = getJwchCourseApi().getCourseState(CookieUtil.id).string()
            emit(data)
        }.flowIO()
    }


    fun getCourses(xq:String,stateHTML:String):Flow<Map<String,String>>{
        return flow {
            info(stateHTML)
            val viewStateMap = parseCourseStateHTML(stateHTML)
            emit(viewStateMap)
        }.flowIO()
    }

    fun getCoursesHTML(viewStateMap:Map<String,String>,xq: String):Flow<List<CourseBean>>{
        return flow {
            val result = getJwchCourseApi().getCourses(
                CookieUtil.id,
                xq,
                viewStateMap["EVENTVALIDATION"] ?: "",
                viewStateMap["VIEWSTATE"] ?: ""
            ).string()
            val data =  parseCoursesHTML(xq, result)
            emit(data)
        }
            .flowIO()
            .catchWithMassage {

            }
    }
    private fun parseCoursesHTML(xueNian: String, result: String): List<CourseBean> {
        debug(result)
        val tempCourses = ArrayList<CourseBean>()
        //解析学年
        val yearStr = xueNian.substring(0, 4)
        val xuenianStr = xueNian.substring(4, 6)
        val year = Integer.parseInt(yearStr)
        val xuenian = Integer.parseInt(xuenianStr)
        val document = Jsoup.parse(result)
        //添加学期列表
        val options = document.select("option")
        debug("getHistoryCourseHtml: Options " + options.toString())
        val optionStr = ArrayList<String>()
        for (element in options) {
            optionStr.add(element.attr("value"))
            info("添加历史学期" + element.attr("value"))
        }
//        if (optionStr.isEmpty()) {
//            throw ApiException("term is empty!")
//        }
//        DataManager.options = optionStr

        val removeLocationPrefix = fun(location: String) = location
            .removePrefix("铜盘")
            .removePrefix("旗山")
        //开始解析课表
        val courseEles =
            document.select("tr[onmouseover=c=this.style.backgroundColor;this.style.backgroundColor='#CCFFaa']")
        for (i in courseEles.indices) {
            val kb = courseEles[i]
            val kclb = kb.select("td")[6].text()
            //免听直接跳过不添加
            if (kclb.contains("免听")) continue
            val titleEle = kb.select("td")[1]
            info("titile:" + titleEle.text())
            val title = titleEle.text()
            val jiaoxueDagang = kb.select("td")[2].select("a")[0].attr("href")
                .replace("javascript:pop1('", JWCH_BASE_URL).replace("');", "")
                .split("&")[0] + "&id={id}"
            val shoukeJihua = kb.select("td")[2].select("a")[1].attr("href")
                .replace("javascript:pop1('", JWCH_BASE_URL).replace("');", "")
                .split("&")[0] + "&id={id}"
            val teacher = kb.select("td")[7].text()
            //解析课程备注:
            val note = kb.select("td")[11].text()

            //解析上课时间
            val timeCou = kb.select("td")[8].html()
            val strings = timeCou.split("<br>").dropLastWhile { it.isEmpty() }.toTypedArray()
            for (string in strings) {
                debug(string)
                val kc = CourseBean()
                if (note.isNotEmpty()) {
                    kc.kcNote = note
                }
                kc.teacher = teacher
                kc.jiaoxueDagang = jiaoxueDagang
                kc.shoukeJihua = shoukeJihua
                kc.kcBackgroundId = i
                kc.kcYear = year
                kc.kcXuenian = xuenian
                kc.kcName = title
                try {
                    val contents = string.split("&nbsp;")
                    val week = contents[0].split("-")
                    val startWeek = Integer.parseInt(week[0])
                    val endWeek = Integer.parseInt(week[1])
                    kc.kcStartWeek = startWeek
                    kc.kcEndWeek = endWeek
                    val weekend = Integer.parseInt(contents[1].substring(2, 3))
                    kc.kcWeekend = weekend

                    when {
                        contents[1].contains("单") -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 4)
                            val time = timeStr.split("-")
                            val startTime = Integer.parseInt(time[0])
                            val endTime = Integer.parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                            kc.kcIsDouble = false

                        }
                        contents[1].contains("双") -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 4)
                            val time = timeStr.split("-")
                            val startTime = Integer.parseInt(time[0])
                            val endTime = Integer.parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                            kc.kcIsSingle = false
                        }
                        else -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 1)
                            val time = timeStr.split("-")
                            val startTime = Integer.parseInt(time[0])
                            val endTime = Integer.parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                        }
                    }

                    val location = contents[2]
                    kc.kcLocation = removeLocationPrefix(location)
                    tempCourses.add(kc)
                } catch (e: Exception) {
                    error("解析出错:$title")
                }
            }
//            if (note.isNotEmpty() && DataManager.courseImportNode) {
//                //解析调课信息
//                val matcher =
//                    Pattern.compile("(\\d{2})周 星期(\\d):(\\d{1,2})-(\\d{1,2})节\\s*调至\\s*(\\d{2})周 星期(\\d):(\\d{1,2})-(\\d{1,2})节\\s*(\\S*)")
//                        .matcher(note)
//                while (matcher.find()) {
//                    val toWeek = matcher.group(5)?.toIntOrNull() ?: 0
//                    val toWeekend = matcher.group(6)?.toIntOrNull() ?: 0
//                    val toStart = matcher.group(7)?.toIntOrNull() ?: 0
//                    val toEnd = matcher.group(8)?.toIntOrNull() ?: 0
//                    val toPlace = matcher.group(9) ?: ""
//                    val kc = CourseBean()
//                    kc.kcNote = note
//                    kc.teacher = teacher
//                    kc.jiaoxueDagang = jiaoxueDagang
//                    kc.shoukeJihua = shoukeJihua
//                    kc.kcBackgroundId = i
//                    kc.kcYear = year
//                    kc.kcXuenian = xuenian
//                    kc.kcStartWeek = toWeek
//                    kc.kcEndWeek = toWeek
//                    kc.kcStartTime = toStart
//                    kc.kcEndTime = toEnd
//                    kc.kcIsSingle = true
//                    kc.kcIsDouble = true
//                    kc.kcLocation = removeLocationPrefix(toPlace)
//                    kc.kcWeekend = toWeekend
//                    kc.kcName = "[调课]$title"
//                    tempCourses.add(kc)
//                }
//            }
        }
        info("history共" + courseEles.size + "个" + " 解析后:" + tempCourses.size + "个")
        return tempCourses
    }
    private fun parseCourseStateHTML(result: String): Map<String, String> {
        val document = Jsoup.parse(result)
        info("获取课表参数")
        //设置常用参数
        val VIEWSTATE = document.getElementById("__VIEWSTATE").attr("value")
        val EVENTVALIDATION = document.getElementById("__EVENTVALIDATION").attr("value")
        val params = HashMap<String, String>()
        params["VIEWSTATE"] = VIEWSTATE
        params["EVENTVALIDATION"] = EVENTVALIDATION
        return params
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
