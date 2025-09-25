package com.example.imagery_vr.support

import com.example.imagery_vr.models.kuisoner_cek
import com.example.imagery_vr.models.kuisoner_pertanyaan
import com.example.imagery_vr.models.kuisoner_response
import com.example.imagery_vr.models.materi_detail_list
import com.example.imagery_vr.models.materi_list
import com.example.imagery_vr.models.survey_response
import com.example.imagery_vr.models.survey_soal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface api_services {
    @GET("/login/{data}")
    fun login(@Path("data") data : String): retrofit2.Call<List<response>>

    @GET("/register/{data}")
    fun register(@Path("data") data : String): retrofit2.Call<response>

    @GET("/materi")
    fun getMateri(): Call<List<materi_list>>

    @GET("/materi/detail/{data}")
    fun getMateriDetail(@Path("data") data : String) : retrofit2.Call<List<materi_detail_list>>

    @GET("/survey-pertanyaan")
    fun getSurveyPertanyaan() : Call<List<survey_soal>>

    @GET("/survey-jawaban/{data}")
    fun getSurveyJawaban(@Path("data") data : String) : retrofit2.Call<survey_response>

    @GET("/kuisoner-cek/{data}")
    fun getKuisonerCek(@Path("data") data : String) : retrofit2.Call<kuisoner_cek>

    @GET("/kuisoner-pertanyaan/{id}")
    fun getKuisonerPertanyaan(@Path("id") id : String) : Call<List<kuisoner_pertanyaan>>

    @GET("/kuisoner-jawaban/{data}")
    fun getKuisonerJawaban(@Path("data") data : String) : retrofit2.Call<kuisoner_response>
}