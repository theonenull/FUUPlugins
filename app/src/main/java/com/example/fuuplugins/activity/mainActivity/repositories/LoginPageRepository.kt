package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.JwchApiService
import com.example.fuuplugins.activity.mainActivity.repositories.BlockLoginPageRepository.getJwchApi
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import com.example.fuuplugins.util.catchWithMassage
import com.example.fuuplugins.util.debug
import com.example.fuuplugins.util.flowDefault
import com.example.fuuplugins.util.flowIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64
import java.util.concurrent.TimeUnit

interface LoginPageRepository{

}

object BlockLoginPageRepository : LoginPageRepository{
    private var jwchApiServiceInstance: JwchApiService? = null
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
    fun getJwchApi(): JwchApiService {
        if (jwchApiServiceInstance == null) {
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

    //登录 step1
    fun loginStudent(
        user: String,
        pass: String,
        captcha: String = "",
        tryTimes: Int = 0,
        theUserDoesNotExistAction: () -> Unit = {},
        captchaVerificationFailedAction: () -> Unit = {},
        thePasswordIsIncorrectAction: () -> Unit = {},
        errorNotExplainedAction: () -> Unit = {},
        theOriginalPasswordIsWeakAction: () -> Unit = {},
        networkErrorAction: () -> Unit = {},
        elseErrorAction:()->Unit = {},
        everyErrorAction:(LoginError)->Unit = {}
    ): Flow<TokenData> {
        val exceptionActions = listOf(
            theUserDoesNotExistAction,
            captchaVerificationFailedAction,
            thePasswordIsIncorrectAction,
            errorNotExplainedAction,
            theOriginalPasswordIsWeakAction,
            networkErrorAction,
        )
        return flow {
            val result = getJwchApi().loginStudent(user, pass, captcha)
            if(!result.isSuccessful){
                throw LoginError.NetworkError.throwable
            }
            val data = result.body()?.string() ?: ""
            LoginError.values().forEach {
                if(data.contains(it.throwable.message.toString())){
                    throw it.throwable
                }
            }
            val token = data.split("var token = \"")[1].split("\";")[0]
            val url = result.raw().request.url.toString()
            emit(TokenData(token = token,url = url))
        }.catchWithMassage { loginThrowable->
            LoginError.values().forEachIndexed { index,error ->
                if(loginThrowable.compareWith(error)){
                    exceptionActions[index].invoke()
                    everyErrorAction.invoke(error)
                }
            }
        }.flowIO()
    }

    //step2 中转，匹配Token
    fun loginByTokenForIdInUrl(
        result : TokenData,
        failedToGetAccount : (Throwable)->Unit = {},
        elseMistake : (Throwable)->Unit = {}
    ):Flow<HashMap<String,String>>{
        return flow {
//            val body = result.body()?.string()  ?: ""
//            val token = body.split("var token = \"")[1].split("\";")[0]
            val token = result.token
            val url = result.url
            val result2 = getJwchApi().loginByToken(token)
            if(result2.code == 200){
                //Step3 获取Url中的id信息
//                val url = result.raw().request.url.toString()
                val id = url.split("id=")[1].split("&")[0]
                val num = url.split("num=")[1].split("&")[0]
                val queryMap = hashMapOf(
                    "id" to id,
                    "num" to num,
                    "ssourl" to "https://jwcjwxt2.fzu.edu.cn",
                    "hosturl" to "https://jwcjwxt2.fzu.edu.cn:81"
                )
                emit(queryMap)
            }
            else if (result2.code == 400 && result2.info.contains("获取account失败")) {
                //400 重新登录一次试试看
                throw Throwable("获取account失败")
            }
        }
    }

    //Step4 用loginCheckXs接口登录
    fun loadCookieData(queryMap:HashMap<String,String>,user: String) : Flow<String>{
        return flow {
            //Step4 用loginCheckXs接口登录
            val result3 = getJwchApi().loginCheckXs(queryMap)
            if (result3.isSuccessful) {
                val body3 = result3.body()!!.string()
                if (body3.contains("福州大学教务处本科教学管理系统")) {
                    val url3 = result3.raw().request.url.toString()
                    CookieUtil.id = url3.split("id=")[1].split("&")[0]
                    CookieUtil.lastUpdateTime = System.currentTimeMillis()
                    emit(user)
                }
            }
        }.flowIO()
    }

    //Step5 检查用户信息 防止串号
    fun checkTheUserInformation(
        user: String,
        serialNumberHandling:()->Unit = {}
    ): Flow<LoginResult> {
        return flow {
            //Step5 检查用户信息 防止串号
            val result = getJwchApi().getInfo(CookieUtil.id).string()
            val check = result.contains(user)
            if (check) {
                emit(LoginResult.LoginSuccess)
            } else {
                //用户信息不对，重新登录试试看
                CookieUtil.clear()
                serialNumberHandling.invoke()
                emit(LoginResult.LoginError)
            }
        }.flowIO()
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
data class TokenData(
    val token : String,
    val url : String
)

enum class LoginError(val throwable: Throwable){
    TheUserDoesNotExist(Throwable("不存在该用户")),
    CaptchaVerificationFailed(Throwable("验证码验证失败")),
    ThePasswordIsIncorrect(Throwable("密码错误")),
    ErrorNotExplained(Throwable("未知错误,但不是网络错误")),
    TheOriginalPasswordIsWeak(Throwable("原密码较弱")),
    NetworkError(Throwable("网络错误"))
}

enum class LoginResult{
    LoginSuccess,
    LoginError
}





fun Throwable.compareWith(t:Throwable):Boolean{
    return this.message == t.message
}

fun Throwable.compareWith(t:LoginError):Boolean{
    return this.message == t.throwable.message
}

