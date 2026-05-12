package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class perangkat_perkembangan_req(
    val identitas           : String,
    val dataCollection      : String
)

data class perangkat_perkembangan_res(
    val code : Int,
    val msg  : String,
    val res  : String
)