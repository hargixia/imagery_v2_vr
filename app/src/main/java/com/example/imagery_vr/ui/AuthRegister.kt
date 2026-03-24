package com.example.imagery_vr.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import com.example.imagery_vr.models.bidang
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import com.google.android.material.textfield.TextInputLayout
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
    var gender : RadioGroup? = null
    lateinit var radio : RadioButton
    private lateinit var tx_bidang      : EditText
    private lateinit var tx_pass        : EditText
    private lateinit var tx_cpass       : EditText
    private lateinit var btn_date       : Button
    private lateinit var btn_regis      : Button
    private lateinit var btn_login      : Button
    private lateinit var tv_error       : TextView

    private lateinit var tglL           : String
    private lateinit var bidang_spinner : Spinner

    val dataList = listOf(
        bidang(1, "Basket"),
        bidang(2, "Sepak Bola"),
        bidang(3, "Voli"),
        bidang(4, "Renang"),
        bidang(5, "Bulutangkis"),
        bidang(6, "Tenis Meja"),
        bidang(7, "Atletik"),
        bidang(8, "Fustal"),
    )

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
        gender = findViewById(R.id.ar_regis_gender)
        tx_pass     = findViewById(R.id.ar_password)
        tx_cpass    = findViewById(R.id.ar_cpassword)
        btn_regis   = findViewById(R.id.ar_register_btn)
        btn_login   = findViewById(R.id.ar_login_btn)
        tv_error    = findViewById(R.id.ar_error)

        tglL        = ""

        bidang_spinner = findViewById(R.id.spinner_regis)

        var bidang_id = 0
        val names = dataList.map { it.nama }
        val adapter = ArrayAdapter(this,R.layout.spinner_list,names)
        adapter.setDropDownViewResource(R.layout.spinner_list)
        bidang_spinner.adapter = adapter

        bidang_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bidang_id = dataList[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bidang_id = 0
            }

        }

        btn_date.setOnClickListener {
            showDatePicker()
        }

        btn_login.setOnClickListener {
            startActivity(Intent(this@AuthRegister, AuthLogin::class.java))
        }

        btn_regis.setOnClickListener {
            val selectOption: Int = gender!!.checkedRadioButtonId
            radio = findViewById(selectOption)

            if (
                tx_username.text.toString()     == "" ||
                tx_name.text.toString()         == "" ||
                tglL                            == "" ||
                tx_pass.text.toString()         == "" ||
                tx_cpass.text.toString()        == ""
                ){
                Toast.makeText(this,"Password Tidak Sama",Toast.LENGTH_SHORT).show()
            }else{
                if(tx_cpass.text.toString() != tx_pass.text.toString()){
                    Toast.makeText(this,"Password Tidak Sama",Toast.LENGTH_SHORT).show()
                }else{
                    var sgender = "L"
                    when(radio.text.toString()){
                        "Laki-Laki" -> sgender = "L"
                        "Perempuan" -> sgender = "P"
                    }
                    val req =   "Register>>"+
                                tx_username.text.toString() + ">>" +
                                tx_name.text.toString() + ">>" +
                                tglL + ">>" +
                                sgender + ">>" +
                                bidang_id.toString() + ">>"+
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