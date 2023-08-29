package com.example.fuuplugins.Base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class BaseRepository {
    inline fun <reified T> createApi(url: String, client: OkHttpClient): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }
}