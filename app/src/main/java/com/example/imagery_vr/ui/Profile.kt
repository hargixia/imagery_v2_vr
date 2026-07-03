package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.imagery_vr.R
import com.example.imagery_vr.models.perkembangan_res
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.deviceSessionManager
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Profile : AppCompatActivity() {

    private lateinit var tv1        : TextView
    private lateinit var tv2        : TextView
    private lateinit var tv3        : TextView
    private lateinit var tv4        : TextView

    val dataParcel                  : Array<String?> = arrayOfNulls(3)
    private val bluetoothAdapter    : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket     : BluetoothSocket? = null
    private val uuid                : UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private lateinit var ds         : SharedPreferences
    private var user_id             : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dInfo       = deviceSessionManager.currentDevice
        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id         = ds.getInt("user_id",0)

        tv1 = findViewById<TextView>(R.id.profil_tv1)
        tv2 = findViewById<TextView>(R.id.profil_tv2)
        tv3 = findViewById<TextView>(R.id.profil_tv3)
        tv4 = findViewById<TextView>(R.id.profil_tv4)

        tv1.text = "Nama Perangkat :${dInfo?.name}"

    }

}