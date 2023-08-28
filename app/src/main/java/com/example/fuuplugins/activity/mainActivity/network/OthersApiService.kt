package com.example.fuuplugins.activity.mainActivity.network

import com.example.fuuplugins.config.EMPTY_ROOM_URL
import com.example.fuuplugins.config.SCHOOL_CALENDAR_URL
import com.example.fuuplugins.config.STATISTICS_BASE_URL
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface OthersApiService {
    @GET("https://jwcjwxt2.fzu.edu.cn:82/week.asp")
    suspend fun getWeekHtml(): ResponseBody

//    @POST("$EMPTY_ROOM_URL/emptyRoom")
//    @FormUrlEncoded
//    suspend fun getEmptyRoom(
//        @Field("time") time: String,
//        @Field("build") build: String,
//        @Field("start") start: String,
//        @Field("end") end: String
//    ): EmptyRoom

    @GET("$SCHOOL_CALENDAR_URL/xl.asp")
    suspend fun getSchoolCalendar(@Query("xq") xq: String): ResponseBody

//    @POST("$STATISTICS_BASE_URL/api/login/validateCode")
//    @Multipart
//    suspend fun validateCode(@Part("validateCode") validateCode: RequestBody): ValidateCodeResponseDto

    //考场
    @POST("/student/xkjg/examination/exam_list.aspx")
    @FormUrlEncoded
    suspend fun getExams(
        @Query("id") id: String,
        @Field("ctl00\$ContentPlaceHolder1\$DDL_xnxq") xuenian: String,
        @Field("__EVENTVALIDATION") event: String,
        @Field("__VIEWSTATE") state: String,
        @Field("ctl00\$ContentPlaceHolder1\$BT_submit") submit: String = "确定"
    ): ResponseBody

    @GET("/student/xkjg/examination/exam_list.aspx")
    suspend fun getExamState(
        @Query("id") id: String
    ): ResponseBody
}
