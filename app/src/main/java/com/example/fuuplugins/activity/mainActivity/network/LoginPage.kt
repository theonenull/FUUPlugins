package com.example.fuuplugins.activity.mainActivity.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UndergraduateApiService {

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

}