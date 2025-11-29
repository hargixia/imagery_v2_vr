package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class perkembangan_res(
    @SerializedName("code") val code : Int,
    @SerializedName("msg")  val msg  : String,
    @SerializedName("res")  val res  : List<perkembangan_detail>
)

data class perkembangan_detail(
    val judul       : String,
    val nama        : String,
    val umur        : String,
    val gender      : String,
    val t_jumlah    : Float,
    val t_max       : Float,
    val t_min       : Float,
    val last_h      : String,
    val last_w      : String,
    val t_avg       : Float,
    val kategori    : String,
    val status      : String,
    val tinggi      : Float,
    val utinggi     : String,
    val data        : List<perkembangan_data_list>
)

data class perkembangan_data_list(
    @SerializedName("no")           val no          : Int,
    @SerializedName("nilai")        val nilai       : Float,
    @SerializedName("kategori")     val kategori    : String,
    @SerializedName("tipe")         val tipe        : String,
    @SerializedName("hari")         val hari        : String,
    @SerializedName("tanggal")      val tanggal     : String,
)