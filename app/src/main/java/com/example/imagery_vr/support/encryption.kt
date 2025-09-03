package com.example.imagery_vr.support

import android.os.Build
import androidx.annotation.RequiresApi

class encryption {
    @RequiresApi(Build.VERSION_CODES.O)
    public fun encob64(oritx : String): String {
        val data = oritx.toByteArray(Charsets.UTF_8)
        val encodeBytes = java.util.Base64.getEncoder().encode(data)
        return String(encodeBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun decob64(encostr : String): String {
        val data = encostr.toByteArray(Charsets.UTF_8)
        val decodeBytes = java.util.Base64.getDecoder().decode(data)
        return String(decodeBytes)
    }

    public fun splitter(data : String) : Array<String> {
        val toarr = data.split(">>").toTypedArray()
        return toarr
    }
}