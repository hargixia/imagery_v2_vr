package com.example.imagery_vr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R

class Kuisoner_Hasil : AppCompatActivity() {
    private lateinit var tv_skor        : TextView
    private lateinit var tv_kategori    : TextView
    private lateinit var progress_bar   : ProgressBar

    private lateinit var btn_dashboard  : Button
    private lateinit var btn_materi     : Button
    private lateinit var ds             : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kuisoner_hasil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tv_skor         = findViewById(R.id.kh_value)
        tv_kategori     = findViewById(R.id.kh_kategori)
        progress_bar    = findViewById(R.id.kh_skor_progress)
        btn_dashboard   = findViewById(R.id.kh_btn_dashboard)
        btn_materi      = findViewById(R.id.kh_btn_materi)

        val skor        = intent.getFloatExtra("skor", 0.0f)
        val kategori    = intent.getStringExtra("kategori")

        tv_skor.text        = skor.toString()
        tv_kategori.text    = kategori
        progress_bar.progress   = skor.toInt()

        btn_dashboard.setOnClickListener {
            startActivity(Intent(this@Kuisoner_Hasil, Dashboard::class.java))
        }
        btn_materi.setOnClickListener {
            startActivity(Intent(this@Kuisoner_Hasil, materi::class.java))
        }
    }
}