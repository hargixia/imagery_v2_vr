package com.example.imagery_vr.support

object deviceSessionManager {

    var connected = false
    var currentDevice : deviceData? = null

    fun getConnect(status: Boolean){
        connected = status
    }

    fun clearData(){
        currentDevice = null
    }
}