package com.example.fuuplugins.activity.mainActivity.network

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExamService {

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