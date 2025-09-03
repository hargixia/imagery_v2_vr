package com.example.imagery_vr.support

import retrofit2.http.GET
import retrofit2.http.Path

interface api_services {
    @GET("/login/{data}")
    fun login(@Path("data") data : String): retrofit2.Call<response>

    @GET("/register/{data}")
    fun register(@Path("data") data : String): retrofit2.Call<response>

}