package com.example.fuuplugins.activity.mainActivity.repositories

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.mainActivity.data.CookieUtil
import com.example.fuuplugins.activity.mainActivity.network.UndergraduateApiService
import com.example.fuuplugins.config.JWCH_BASE_URL
import com.example.fuuplugins.network.OkHttpUtil
import com.example.fuuplugins.util.debug
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
    ):Flow<String>{
        val exceptionActions = listOf(
            theUserDoesNotExistAction,
            captchaVerificationFailedAction,
            thePasswordIsIncorrectAction,
            errorNotExplainedAction,
            theOriginalPasswordIsWeakAction,
            networkErrorAction
        )
        return flow {
            val result = getJwchApi().loginStudent(user, pass, captcha)
            if(!result.isSuccessful){
                throw LoginError.NetworkError.throwable
            }
            val data = (result.body()?.string() ?: "")
            LoginError.values().forEach {
                if(data.contains(it.throwable.message.toString())){
                    throw it.throwable
                }
            }
            val token = data.split("var token = \"")[1].split("\";")[0]
            emit(token)
        }
            .retryWhen { cause, attempt ->
                cause.compareWith(LoginError.NetworkError) && attempt <= tryTimes
            }
            .catch { loginThrowable->
                LoginError.values().forEachIndexed { index,error ->
                    if(loginThrowable.compareWith(error)){
                        exceptionActions[index].invoke()
                        everyErrorAction.invoke(error)
                    }
                }
                debug(loginThrowable.message.toString())
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


enum class LoginError(val throwable: Throwable){
    TheUserDoesNotExist(Throwable("不存在该用户")),
    CaptchaVerificationFailed(Throwable("验证码验证失败")),
    ThePasswordIsIncorrect(Throwable("密码错误")),
    ErrorNotExplained(Throwable("未知错误,但不是网络错误")),
    TheOriginalPasswordIsWeak(Throwable("原密码较弱")),
    NetworkError(Throwable("网络错误"))
}






fun Throwable.compareWith(t:Throwable):Boolean{
    return this.message == t.message
}

fun Throwable.compareWith(t:LoginError):Boolean{
    return this.message == t.throwable.message
}

