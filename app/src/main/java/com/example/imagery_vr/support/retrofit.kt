package com.example.imagery_vr.support

import android.icu.util.TimeUnit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofit {
    private const val BASE_URL = "https://snow-turtle-672937.hostingersite.com/"
    //private const val BASE_URL = "https://linen-walrus-983142.hostingersite.com/"
    //private const val BASE_URL = "http://10.72.1.120/"

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .callTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .build()

    val instance : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}