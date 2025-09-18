package com.example.imagery_vr.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import com.example.imagery_vr.models.users
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.retrofit
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Array
import java.time.LocalDate
import java.util.Locale

class AuthLogin : AppCompatActivity() {

    private lateinit var tx_username    : EditText
    private lateinit var tx_passwrod    : EditText
    private lateinit var tv_error       : TextView

    private lateinit var btn_login      : Button
    private lateinit var btn_register   : Button

    private lateinit var ds             : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apis        = retrofit.instance.create(api_services::class.java)
        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)

        tx_username     = findViewById(R.id.al_username)
        tx_passwrod     = findViewById(R.id.al_password)
        tv_error        = findViewById(R.id.al_error_tx)

        btn_login       = findViewById(R.id.al_login_btn)
        btn_register    = findViewById(R.id.al_register_btn)

        btn_login.setOnClickListener {
            if (
                tx_passwrod.text.toString() == "" ||
                tx_passwrod.text.toString() == ""
            ){
                tv_error.setText("Harap Isi Username dan Password nya...")
            }else{
                tv_error.setText(" ")
                val req     = tx_username.text.toString() + ">>" + tx_passwrod.text.toString()
                val enco    = encryption().encob64(req)
                apis.login(enco).enqueue(object : Callback<response>{
                    override fun onResponse(call: Call<response>, response: Response<response>) {
                        if(response.isSuccessful){
                            response.body()?.let {
                                try {
                                    val jsonstr = encryption().decob64(it.data)
                                    val jsonarr = encryption().splitter(jsonstr)
                                    val userdata = Gson().fromJson(jsonarr[1],users::class.java)

                                    if (it.status == "0"){
                                        savedata(userdata)
                                        tv_error.setText("")
                                        startActivity(Intent(this@AuthLogin,Dashboard::class.java))
                                    }else if (it.status == "2"){
                                        savedata(userdata)
                                        tv_error.setText("")
                                        startActivity(Intent(this@AuthLogin,Survey::class.java))
                                    }else{
                                        tv_error.setText(jsonarr[0])
                                    }
                                }catch (e : Exception){
                                    tv_error.setText("Username atau Password Salah.")
                                }
                            }
                        }else{
                            tv_error.setText("Error res : ${response.raw()}")
                        }
                    }

                    override fun onFailure(call: Call<response>, t: Throwable) {
                        tv_error.setText("Error Fail : ${t.message}")
                    }

                })
            }
        }

        btn_register.setOnClickListener {
            startActivity(Intent(this@AuthLogin,AuthRegister::class.java))
        }
    }

    fun savedata(data : users){
        val ndate       = LocalDate.now()
        val exp_date    = ndate.plusDays(1)
        var dse         = ds.edit()

        dse.putString("login_exp",exp_date.toString())
        dse.putInt("login_status",1)
        dse.putInt("user_id",data.id)
        dse.putString("username",data.username)
        dse.putString("nama",data.nama)
        dse.putInt("bidang_id",data.id_bidang)
        dse.apply()

        Toast.makeText(this@AuthLogin,"Selamat Datang ${data.username} di IMAGERY.",Toast.LENGTH_LONG).show()
        onDestroy()
    }

}