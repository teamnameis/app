package com.example.kimono.api

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.teamnameis.be.Flame
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SendApi {
    @POST("/")
    fun send(@Body flame: Flame): Single<Flame>
}

object Api {
    private const val BASE_URL = "http://10.0.1.14:1234"
    private val okHttpClient: OkHttpClient = OkHttpClient
        .Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(ProtoConverterFactory.create())
        .client(okHttpClient)
        .build()

    val sendApi = retrofit.create(SendApi::class.java)
}