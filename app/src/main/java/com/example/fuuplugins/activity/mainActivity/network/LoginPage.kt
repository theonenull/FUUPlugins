package com.example.fuuplugins.activity.mainActivity.network

import okhttp3.ResponseBody
import retrofit2.http.GET

interface UndergraduateApiService {
    //获取验证码
    @GET("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp")
    suspend fun getVerifyCode(): ResponseBody

}