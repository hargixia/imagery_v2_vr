package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class kuisoner_pertanyaan(
    @SerializedName("code") val code : String,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<kuisoner_pertanyaan_items>
)

data class kuisoner_pertanyaan_items(
    @SerializedName("id")val id             : Int,
    @SerializedName("no")val no             : Int,
    @SerializedName("soal")val soal         : String,
    @SerializedName("id_materi")val idm     : String,
    @SerializedName("desc")val desc         : String,
    @SerializedName("audio")val audio       : String,
    @SerializedName("video")val video       : String,
    @SerializedName("img")val img           : String,
)
