package com.example.imagery_vr.ui

import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_survey

class Survey : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_survey)

        val rv_1 = findViewById<RecyclerView>(R.id.survey_rv_1)

        rv_1.layoutManager = LinearLayoutManager(this)
        //val adapter = adapter_survey()
        //rv_1.adapter = adapter

    }
}