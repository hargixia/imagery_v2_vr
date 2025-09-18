package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class materi_detail_list(
    @SerializedName("msg") val msg  : String,
    @SerializedName("res")val res   : List<materi_detail_items>
)

data class materi_detail_items(
    @SerializedName("id")val id             : Int,
    @SerializedName("id_materi")val idm     : String,
    @SerializedName("desc")val desc         : String,
    @SerializedName("audio")val audio       : String,
    @SerializedName("video")val video       : String,
    @SerializedName("img")val img           : String,
)