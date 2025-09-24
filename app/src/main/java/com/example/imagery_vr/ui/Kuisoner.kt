package com.example.imagery_vr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.retrofit

class Kuisoner : AppCompatActivity() {

    private lateinit var judul          : TextView
    private lateinit var nama           : TextView
    private lateinit var materi_tv      : TextView
    private lateinit var rv1            : RecyclerView
    private lateinit var btn_kirim      : Button
    private lateinit var ds             : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kuisoner)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ds              = getSharedPreferences("IMGV1",MODE_PRIVATE)
        val apis        = retrofit.instance.create(api_services::class.java)
        val idm         = intent.getIntExtra("m_id",0)
        val jm          = intent.getStringExtra("m_judul")
        val mode_m      = intent.getStringExtra("mode")

        val user_id     = ds.getInt("user_id",0)
        val user_name   = ds.getString("nama","user")

        judul           = findViewById(R.id.kuisoner_title)
        nama            = findViewById(R.id.kuisoner_tx_nama)
        materi_tv       = findViewById(R.id.kuisoner_tx_materi)
        rv1             = findViewById(R.id.kuisoner_rv_1)
        btn_kirim       = findViewById(R.id.kuisoner_btn_kirim)

        if(user_id == 0){
            Toast.makeText(this@Kuisoner,"Tidak Ada Info User, Silahkan Login Ulang.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Kuisoner, materi::class.java))
        }else{
            judul.text = "Pengisian $mode_m"
            nama.text = "Nama : $user_name"
            materi_tv.text = "Materi : $jm"

        }

    }
}