package com.example.imagery_vr.support

import com.example.imagery_vr.models.kuisoner_cek
import com.example.imagery_vr.models.kuisoner_pertanyaan
import com.example.imagery_vr.models.kuisoner_response
import com.example.imagery_vr.models.materi_detail_list
import com.example.imagery_vr.models.materi_list
import com.example.imagery_vr.models.perangkat_perkembangan_req
import com.example.imagery_vr.models.perangkat_perkembangan_res
import com.example.imagery_vr.models.perkembangan_res
import com.example.imagery_vr.models.survey_response
import com.example.imagery_vr.models.survey_soal
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface api_services {
    @GET("/api/login/{data}")
    fun login(@Path("data") data : String): retrofit2.Call<List<response>>

    @GET("/api/register/{data}")
    fun register(@Path("data") data : String): retrofit2.Call<response>

    @GET("/api/materi/{data}")
    fun getMateri(@Path("data") data: String): Call<List<materi_list>>

    @GET("/api/materi_detail/{data}")
    fun getMateriDetail(@Path("data") data : String) : retrofit2.Call<List<materi_detail_list>>

    @GET("/api/survey-pertanyaan")
    fun getSurveyPertanyaan() : Call<List<survey_soal>>

    @GET("/api/survey-jawaban/{data}")
    fun getSurveyJawaban(@Path("data") data : String) : retrofit2.Call<survey_response>

    @GET("/api/kuisoner_cek/{data}")
    fun getKuisonerCek(@Path("data") data : String) : Call<List<kuisoner_cek>>

    @GET("/api/kuisoner_pertanyaan/{data}")
    fun getKuisonerPertanyaan(@Path("data") data : String) : Call<List<kuisoner_pertanyaan>>

    @GET("/api/kuisoner_jawab/{data}")
    fun getKuisonerJawaban(@Path("data") data : String) : Call<List<kuisoner_response>>

    @GET("/api/perkembangan/{data}")
    fun getPerkembangan(@Path("data") data : String) : Call<List<perkembangan_res>>

    @GET("/api/perkembangan/perangkat/input/{data}")
    fun getPerkembangan_perangkatIO(@Path("data") data : String) : Call<List<perkembangan_res>>

    @POST("/api/perkembangan/perangkat/in")
    fun getPerkembangan_perangkatIN(@Body req : perangkat_perkembangan_req) : Call<perangkat_perkembangan_res>
}