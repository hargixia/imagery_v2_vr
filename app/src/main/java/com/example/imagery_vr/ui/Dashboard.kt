package com.example.imagery_vr.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R

class Dashboard : AppCompatActivity() {

    private lateinit var btn_logout         : Button
    private lateinit var btn_test           : Button
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

        btn_logout      = findViewById(R.id.db_logout_btn)
        btn_test        = findViewById(R.id.db_btn_test)

        btn_logout.setOnClickListener {
            logout()
        }

        btn_test.setOnClickListener {
            startActivity(Intent(this@Dashboard, Survey::class.java))
        }

    }

    fun logout(){
        val dse         = ds.edit()
        dse.clear()
        startActivity(Intent(this@Dashboard,splashScreen::class.java))
        finish()
    }
}