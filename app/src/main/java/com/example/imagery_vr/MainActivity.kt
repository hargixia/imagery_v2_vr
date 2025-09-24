package com.example.imagery_vr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.LocaleData
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.ui.AuthLogin
import com.example.imagery_vr.ui.Dashboard
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val calendar            = Calendar.getInstance()
    private lateinit var ds         : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val ndate = LocalDate.now()

            ds                  = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
            val ds_login_exp    = ds.getString("login_exp","2025-01-01")
            val ds_login_stat   = ds.getInt("login_status",0)

            if(ds_login_stat == 0 && ds_login_exp == ndate.toString()){
                startActivity(Intent(this@MainActivity,AuthLogin::class.java))
            }else{
                startActivity(Intent(this@MainActivity,Dashboard::class.java))
            }

            Toast.makeText(this@MainActivity,"ref : ${ds_login_stat}",Toast.LENGTH_LONG).show()

        },3000L)
    }
}