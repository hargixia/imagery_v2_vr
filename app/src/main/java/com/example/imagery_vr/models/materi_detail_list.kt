package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class materi_detail_list(
    @SerializedName("code") val code : String,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<materi_detail_items>
)

data class materi_detail_items(
    @SerializedName("id")val id                 : Int,
    @SerializedName("judul")val judul           : String,
    @SerializedName("deskripsi")val deskripsi   : String,
    @SerializedName("tipe")val tipe             : String,
    @SerializedName("isi")val isi               : String,
    @SerializedName("id_materi")val idm         : Int,
)