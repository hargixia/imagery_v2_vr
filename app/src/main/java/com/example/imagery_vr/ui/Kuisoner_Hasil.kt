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
    private lateinit var tv_nilai        : TextView
    private lateinit var tv_kategori    : TextView
    private lateinit var tv_nama    : TextView
    private lateinit var tv_materi    : TextView
    private lateinit var tv_hari    : TextView
    private lateinit var tv_tanggal    : TextView
    private lateinit var tv_rekomendasi    : TextView

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
        ds              = getSharedPreferences("IMGV1",MODE_PRIVATE)
        val user_id     = ds.getInt("user_id",0)
        val user_name   = ds.getString("nama","user")

        tv_nilai         = findViewById(R.id.kh_value)
        tv_kategori     = findViewById(R.id.kh_kategori)
        tv_nama         = findViewById(R.id.kh_tx_nama)
        tv_materi     = findViewById(R.id.kh_tx_materi)
        tv_hari        = findViewById(R.id.kh_tx_hari)
        tv_tanggal         = findViewById(R.id.kh_tx_tanggal)
        tv_rekomendasi     = findViewById(R.id.kh_tx_rekomendasi)

        progress_bar    = findViewById(R.id.kh_skor_progress)
        btn_dashboard   = findViewById(R.id.kh_btn_dashboard)
        btn_materi      = findViewById(R.id.kh_btn_materi)

        val nilai           = intent.getFloatExtra("nilai",3f)
        val kategori        = intent.getStringExtra("kategori")
        val rekomendasi     = intent.getStringExtra("rekomendasi")
        val tanggal         = intent.getStringExtra("tanggal")
        val hari            = intent.getStringExtra("hari")

        val materi_judul    = intent.getStringExtra("materi")

        tv_nilai.text               = nilai.toString()
        tv_kategori.text            = kategori

        tv_nama.text                = ":\t" + user_name
        tv_materi.text              = ":\t" + materi_judul
        tv_tanggal.text             = ":\t" + tanggal
        tv_hari.text                = ":\t" + hari

        tv_rekomendasi.text         = rekomendasi

        progress_bar.progress   = nilai.toInt()

        btn_dashboard.setOnClickListener {
            startActivity(Intent(this@Kuisoner_Hasil, Dashboard::class.java))
        }
        btn_materi.setOnClickListener {
            startActivity(Intent(this@Kuisoner_Hasil, materi::class.java))
        }
    }
}