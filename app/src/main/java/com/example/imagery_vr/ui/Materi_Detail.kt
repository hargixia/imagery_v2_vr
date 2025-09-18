package com.example.imagery_vr.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R

class Materi_Detail : AppCompatActivity() {

    private lateinit var tv_judul       : TextView
    private lateinit var tv_desc        : TextView
    private lateinit var rv_1           : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id      = intent.getIntExtra("m_id",0)
        val judul   = intent.getStringExtra("m_judul")
        val desc    = intent.getStringExtra("m_desc")

        tv_judul    = findViewById(R.id.md_Judul)
        tv_desc     = findViewById(R.id.md_desc)
        rv_1        = findViewById(R.id.md_rv_1)

        tv_judul.setText(judul)
        tv_desc.setText(desc)

    }
}