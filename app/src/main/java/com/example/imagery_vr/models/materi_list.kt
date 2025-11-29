package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class materi_list(
    @SerializedName("code") val code : String,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<materi_items>
)

data class materi_items(
    @SerializedName("id")           val id      : Int,
    @SerializedName("judul")        val judul   : String,
    @SerializedName("deskripsi")    val desc    : String
)