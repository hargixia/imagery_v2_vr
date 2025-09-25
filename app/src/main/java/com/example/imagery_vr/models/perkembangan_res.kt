package com.example.imagery_vr.models

import com.google.gson.annotations.SerializedName

data class perkembangan_res(
    val status      : Int,
    val msg         : String,
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
    val data        : List<perkembangan_data_list>,
    val u_tn        : String,
    val u_tv        : String,
    val u_tf        : String
)

data class perkembangan_data_list(
    @SerializedName("no")           val id          : Int,
    @SerializedName("nilai")        val nilai       : Float,
    @SerializedName("kategori")     val kategori    : String,
    @SerializedName("hari")         val hari        : String,
    @SerializedName("waktu")        val waktu       : String,
)