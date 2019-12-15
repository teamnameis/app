package com.example.kimono.api

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.teamnameis.be.Flame
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SendHttpApi {
    @POST("/")
    fun send(@Body flame: Frame): Single<Frame>
}
data class Frame(
    val id:Int,
    val data: String
)
object HttpApi {
    private const val BASE_URL = "http://47.74.3.96:30000"
    private val okHttpClient: OkHttpClient = OkHttpClient
        .Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val sendApi = retrofit.create(SendHttpApi::class.java)
}