package com.example.imagery_vr.models

import com.example.imagery_vr.ui.Survey
import kotlinx.serialization.Serializable

@Serializable
data class users(
    val id              : Int,
    val username        : String,
    val nama            : String,
    val tanggal_lahir   : String,
    val jenis_kelamin   : String,
    val id_bidang       : Int,
    val foto            : String,
    val sc              : String
)
