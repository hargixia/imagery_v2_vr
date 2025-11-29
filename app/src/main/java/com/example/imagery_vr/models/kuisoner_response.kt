package com.example.imagery_vr.models

import android.R
import com.google.gson.annotations.SerializedName

data class kuisoner_response (
    @SerializedName("code") val code : String,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<kuisoner_response_list>
)

data class kuisoner_response_list(
    @SerializedName("total_nilai")  val total_nilai : Int,
    @SerializedName("nilai")        val nilai       : Float,
    @SerializedName("rekomendasi")  val rekomendasi : String,
    @SerializedName("kategori")     val kategori    : String,
    @SerializedName("tanggal")      val tanggal     : String,
    @SerializedName("hari")         val hari        : String
)