package com.example.imagery_vr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_kuisoner
import com.example.imagery_vr.models.kuisoner_jawaban
import com.example.imagery_vr.models.kuisoner_pertanyaan
import com.example.imagery_vr.models.kuisoner_response
import com.example.imagery_vr.models.survey_jawaban
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Kuisoner : AppCompatActivity() {

    private lateinit var judul          : TextView
    private lateinit var nama           : TextView
    private lateinit var materi_tv      : TextView
    private lateinit var rv1            : RecyclerView
    private lateinit var btn_kirim      : Button
    private lateinit var ds             : SharedPreferences

    private lateinit var adapter        : adapter_kuisoner

    private var k_jawaban               : List<kuisoner_jawaban> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kuisoner)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ds              = getSharedPreferences("IMGV1",MODE_PRIVATE)
        val apis        = retrofit.instance.create(api_services::class.java)
        val idm         = intent.getIntExtra("m_id",0)
        val jm          = intent.getStringExtra("m_judul")
        val mode_m      = intent.getStringExtra("mode")

        val user_id     = ds.getInt("user_id",0)
        val user_name   = ds.getString("nama","user")

        judul           = findViewById(R.id.kuisoner_title)
        nama            = findViewById(R.id.kuisoner_tx_nama)
        materi_tv       = findViewById(R.id.kuisoner_tx_materi)
        rv1             = findViewById(R.id.kuisoner_rv_1)
        btn_kirim       = findViewById(R.id.kuisoner_btn_kirim)

        rv1.layoutManager = LinearLayoutManager(this)

        if(user_id == 0){
            Toast.makeText(this@Kuisoner,"Tidak Ada Info User, Silahkan Login Ulang.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@Kuisoner, materi::class.java))
        }else{
            judul.text = "Pengisian $mode_m"
            nama.text = "Nama : $user_name"
            materi_tv.text = "Materi : $jm"

        }

        apis.getKuisonerPertanyaan(idm.toString()).enqueue(object : Callback<List<kuisoner_pertanyaan>>{
            override fun onResponse(
                call: Call<List<kuisoner_pertanyaan>?>,
                response: Response<List<kuisoner_pertanyaan>?>
            ) {
                if(response.isSuccessful){
                    val parcel = response.body()
                    if(parcel != null){
                        adapter = adapter_kuisoner(parcel,user_id,idm){jawabans ->
                            k_jawaban = jawabans
                        }
                        rv1.adapter = adapter
                    }else{
                        Toast.makeText(this@Kuisoner,"Null", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(
                call: Call<List<kuisoner_pertanyaan>?>,
                t: Throwable
            ) {
                Toast.makeText(this@Kuisoner,"Error -> ${t.message}", Toast.LENGTH_LONG).show()
            }

        })

        btn_kirim.setOnClickListener {
            var req = "kj>>" + user_id.toString() + ">>" + idm.toString()
            for(i in k_jawaban){
                req += ">>" + i.value
            }
            val enc = encryption().encob64(req)
            apis.getKuisonerJawaban(enc).enqueue(object : Callback<kuisoner_response>{
                override fun onResponse(
                    call: Call<kuisoner_response?>,
                    response: Response<kuisoner_response?>
                ) {
                    if(response.isSuccessful){
                        val data = response.body()
                        if(data != null){
                            val intent = Intent(this@Kuisoner, Kuisoner_Hasil::class.java).apply {
                                putExtra("skor",data.skor)
                                putExtra("kategori",data.kategori)
                            }
                            Toast.makeText(this@Kuisoner,data.skor.toString(), Toast.LENGTH_LONG).show()
                            startActivity(intent)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<kuisoner_response?>,
                    t: Throwable
                ) {
                    Toast.makeText(this@Kuisoner,"Error -> ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}