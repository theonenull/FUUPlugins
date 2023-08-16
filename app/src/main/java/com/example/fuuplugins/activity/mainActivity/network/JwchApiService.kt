package com.example.fuuplugins.activity.mainActivity.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface JwchApiService {

    //获取验证码
    @GET("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp")
    suspend fun getVerifyCode(): ResponseBody

    //登录
    @POST("https://jwcjwxt1.fzu.edu.cn/logincheck.asp")
    @FormUrlEncoded
    @Headers("Referer:https://jwch.fzu.edu.cn/html/login/1.html", "Origin:https://jwch.fzu.edu.cn")
    suspend fun loginStudent(
        @Field("muser") user: String,
        @Field("passwd") pass: String,
        @Field("VerifyCode") verifyCode: String
    ): Response<ResponseBody>


    @POST("https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin")
    @Headers("X-Requested-With:XMLHttpRequest")
    @FormUrlEncoded
    suspend fun loginByToken(@Field("token") token: String): JwchTokenLoginResponseDto

    @GET("https://jwcjwxt2.fzu.edu.cn:81/loginchk_xs.aspx")
    suspend fun loginCheckXs(@QueryMap map: Map<String, String>): Response<ResponseBody>

    //名字,专业等
    @GET("/jcxx/xsxx/StudentInformation.aspx")
    suspend fun getInfo(
        @Query("id") id: String
    ): ResponseBody
}

data class JwchTokenLoginResponseDto(
    var code: Int, // 200
    var info: String // 登录成功
)