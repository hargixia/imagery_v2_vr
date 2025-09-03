package com.example.imagery_vr.ui

import android.app.DatePickerDialog
import android.content.Intent
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
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AuthRegister : AppCompatActivity() {

    private val calendar                = Calendar.getInstance()

    private lateinit var tx_username    : EditText
    private lateinit var tx_name        : EditText
    private lateinit var tx_tglL        : TextView
    private lateinit var tx_jk          : EditText
    private lateinit var tx_bidang      : EditText
    private lateinit var tx_pass        : EditText
    private lateinit var tx_cpass       : EditText
    private lateinit var btn_date       : Button
    private lateinit var btn_regis      : Button
    private lateinit var tv_error       : TextView

    private lateinit var tglL           : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apis = retrofit.instance.create(api_services::class.java)

        tx_username = findViewById(R.id.ar_username)
        tx_name     = findViewById(R.id.ar_nama)
        tx_tglL     = findViewById(R.id.ar_date)
        btn_date    = findViewById(R.id.ar_date_btn)
        tx_jk       = findViewById(R.id.ar_jenis_kelamin)
        tx_bidang   = findViewById(R.id.ar_Bidang)
        tx_pass     = findViewById(R.id.ar_password)
        tx_cpass    = findViewById(R.id.ar_cpassword)
        btn_regis   = findViewById(R.id.ar_register_btn)
        tv_error    = findViewById(R.id.ar_error)

        tglL        = ""

        btn_date.setOnClickListener {
            showDatePicker()
        }

        btn_regis.setOnClickListener {
            if (
                tx_username.text.toString()     == "" ||
                tx_name.text.toString()         == "" ||
                tglL                            == "" ||
                tx_jk.text.toString()           == "" ||
                tx_bidang.text.toString()       == "" ||
                tx_pass.text.toString()         == "" ||
                tx_cpass.text.toString()        == ""
                ){
                Toast.makeText(this,"Password Tidak Sama",Toast.LENGTH_SHORT).show()
            }else{
                if(tx_cpass.text.toString() != tx_pass.text.toString()){
                    Toast.makeText(this,"Password Tidak Sama",Toast.LENGTH_SHORT).show()
                }else{
                    val req =   tx_username.text.toString() + ">>" +
                                tx_name.text.toString() + ">>" +
                                tglL + ">>" +
                                tx_jk.text.toString() + ">>" +
                                tx_bidang.text.toString() + ">>" +
                                tx_cpass.text.toString()

                    apis.register(encryption().encob64(req)).enqueue(object : Callback<response>{
                        override fun onResponse(
                            call: Call<response>,
                            response: Response<response>
                        ) {
                            if(response.isSuccessful){
                                Toast.makeText(this@AuthRegister,"Berhasil Registrasi User.",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@AuthRegister,AuthLogin::class.java))
                            }else{
                                tv_error.setText("Error respon : ${response.raw()}")
                            }
                        }

                        override fun onFailure(call: Call<response>, t: Throwable) {
                            tv_error.setText("Error fail : ${t.message}")
                        }
                    })
                }
            }
        }

    }

    private fun showDatePicker() {
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                tx_tglL.text = "Tanggal Lahir : $formattedDate "
                tglL = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }
}