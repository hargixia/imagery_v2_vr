package com.example.imagery_vr.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_materi
import com.example.imagery_vr.models.materi_list
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class materi : AppCompatActivity() {

    private lateinit var tv1     : TextView
    private lateinit var rv      : RecyclerView
    private lateinit var adapter : adapter_materi
    private lateinit var ds      : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ds              = getSharedPreferences("IMGV1",MODE_PRIVATE)
        val user_id     = ds.getInt("user_id",0)
        val app         = intent.getStringExtra("App")
        val appVal      = intent.getIntExtra("AppVal",0)

        tv1 = findViewById(R.id.materi_tv_1)
        tv1.text = "Daftar Materi Imagery \n ${app}"

        rv = findViewById(R.id.materi_rv_1)
        rv.layoutManager = LinearLayoutManager(this)

        val apis = retrofit.instance.create(api_services::class.java)
        val req     = "m>>"+ appVal.toString()
        val enco    = encryption().encob64(req)
        apis.getMateri(enco).enqueue(object : Callback<List<materi_list>>{
            override fun onResponse(
                call: Call<List<materi_list>?>,
                response: Response<List<materi_list>?>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    if(data != null){
                        adapter = adapter_materi(data[0].res, user_id,appVal)
                        rv.adapter =adapter
                        //tv1.text = data.toString()
                    }
                }
            }

            override fun onFailure(
                call: Call<List<materi_list>?>,
                t: Throwable
            ) {
                Toast.makeText(this@materi,"error : " + t.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }
}