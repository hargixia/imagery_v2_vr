package com.example.imagery_vr.models

data class perangkat_in_req(
    val identitas           : String,
    val dataCollection      : String
)

data class perangkat_in_res(
    val code : Int,
    val msg  : String,
    val res  : String
)


data class perangkat_akses_catat_req(
    val mode                : String,
    val perangkat           : String,
    val id_materi_detail    : String,
    val id_user             : String
)

data class perangkat_akses_catat_res(
    val code    : Int,
    val msg     : String,
    val res     : List<perangkat_akses_catat_items>
)

data class perangkat_akses_catat_items(
    val id : Int,
    val pos: Int,
    val nama_perangkat : String
)

data class perangkat_akses_req(
    val mode                : String,
    val id_materi_detail    : String,
    val id_user             : String
)

data class perangkat_akses_res(
    val code    : Int,
    val msg     : String,
    val res     : List<perangkat_akses_list>
)


data class perangkat_akses_list(
    val id              : Int,
    val pos             : Int,
    val created_at      : String
)

data class perangkat_data_req(
    val mode            : String,
    val id_materi_akses : String,
    val id_user         : String
)

data class perangkat_data_res(
    val code    : Int,
    val msg     : String,
    val res     : List<perangkat_data_list>
)

data class perangkat_get_req(
    val mode    : String,
    val id_data : String
)

data class perangkat_get_res(
    val code    : Int,
    val msg     : String,
    val res     : List<perangkat_data_list>
)

data class perangkat_data_list(
    val id                  : Int,
    val bpm                 : Double,
    val gsr                 : Double,
    val suhu                : Double,
    val waktu_perangkat     : String,
    val tanggal_perangkat   : String,
    val id_materi_akses     : Int
)