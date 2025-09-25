package com.example.imagery_vr.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.MainActivity
import com.example.imagery_vr.R
import java.time.LocalDate

class Dashboard : AppCompatActivity() {

    private lateinit var db_tv1             : TextView
    private lateinit var btn_logout         : Button
    private lateinit var btn_survey         : Button
    private lateinit var btn_materi         : Button
    private lateinit var ds                 : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        val ds_nama     = ds.getString("nama","user")
        val ds_id       = ds.getInt("user_id",0)
        val survey_count= ds.getInt("survey_count",0)

        db_tv1          = findViewById(R.id.db_tv1)
        btn_logout      = findViewById(R.id.db_logout_btn)
        btn_survey        = findViewById(R.id.db_btn_survey)
        btn_materi      = findViewById(R.id.db_btn_materi)

        db_tv1.text = "Nama User : $ds_nama"

        if (survey_count > 2){
            startActivity(Intent(this@Dashboard, Survey::class.java))
        }

        btn_logout.setOnClickListener {
            logout()
        }

        btn_survey.setOnClickListener {
            startActivity(Intent(this@Dashboard, Survey::class.java))
        }

        btn_materi.setOnClickListener {
            startActivity(Intent(this@Dashboard, materi::class.java))
        }
    }

    fun logout(){
        val dse         = ds.edit()
        dse.clear()
        dse.apply()
        startActivity(Intent(this@Dashboard, MainActivity::class.java))
        finish()
    }
}