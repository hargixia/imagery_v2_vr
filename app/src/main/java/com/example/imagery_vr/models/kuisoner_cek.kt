package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class kuisoner_cek(
    @SerializedName("code") val code : Int,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<kc_junk>
)

data class kc_junk(
    @SerializedName("code") val code : Int,
)
