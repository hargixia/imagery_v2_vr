package com.example.imagery_vr.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_perangkat_data
import com.example.imagery_vr.adapters.adapter_perangkat_data2
import com.example.imagery_vr.models.perangkat_akses_req
import com.example.imagery_vr.models.perangkat_data_req
import com.example.imagery_vr.models.perangkat_data_res
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var ds                 : SharedPreferences
private var user_id                     : Int = 0

private lateinit var rv_1               : RecyclerView
private lateinit var tv_1               : TextView
private lateinit var adapter            : adapter_perangkat_data2

class perangkat_data_2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perangkat_data2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ds                  = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id             = ds.getInt("user_id",0)
        val apis            = retrofit.instance.create(api_services::class.java)

        tv_1 = findViewById(R.id.pd2_tv_1)
        rv_1 = findViewById(R.id.pd2_rv)
        rv_1.layoutManager = LinearLayoutManager(this)

        val mode    = encryption().encob64("perkembangan_perangkat_data")
        val ida     = intent.getIntExtra("ida",0)
        val eida    = encryption().encob64(ida.toString())
        val idu     = encryption().encob64(user_id.toString())

        val parcel = perangkat_data_req(
            mode,
            eida,
            idu
        )

        apis.dataPerangkat(parcel).enqueue(object : Callback<perangkat_data_res> {
            override fun onResponse(
                p0: Call<perangkat_data_res?>,
                p1: Response<perangkat_data_res?>
            ) {
                if(p1.isSuccessful){
                    val data = p1.body()
                    if(data != null){
                        adapter = adapter_perangkat_data2(data.res)
                        rv_1.adapter = adapter
                        Log.d("succceess","Oke : ${data.msg}")
                    }
                }
            }

            override fun onFailure(
                p0: Call<perangkat_data_res?>,
                p1: Throwable
            ) {
                Log.e("Throw","Error : ${p1.message}")
            }
        })

    }
}