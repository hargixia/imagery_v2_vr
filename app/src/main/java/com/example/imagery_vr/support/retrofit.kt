package com.example.imagery_vr.support

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofit {
    private const val BASE_URL = "https://linen-walrus-983142.hostingersite.com/"
    //private const val BASE_URL = "http://10.72.1.120/"

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val instance : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}