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
import com.example.imagery_vr.adapters.adapter_mater_detail
import com.example.imagery_vr.adapters.adapter_materi
import com.example.imagery_vr.models.materi_detail_list
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Materi_Detail : AppCompatActivity() {

    private lateinit var tv_judul           : TextView
    private lateinit var tv_desc            : TextView
    private lateinit var rv_1               : RecyclerView
    private lateinit var btn_kuisoner       : Button
    private lateinit var btn_perkembangan   : Button
    private lateinit var adapter            : adapter_mater_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apis = retrofit.instance.create(api_services::class.java)

        val id              = intent.getIntExtra("m_id",0)
        val judul           = intent.getStringExtra("m_judul")
        val desc            = intent.getStringExtra("m_desc")

        tv_judul            = findViewById(R.id.md_Judul)
        tv_desc             = findViewById(R.id.md_desc)
        rv_1                = findViewById(R.id.md_rv_1)
        btn_kuisoner        = findViewById(R.id.md_kuisoner_btn)
        btn_perkembangan    = findViewById(R.id.md_btn_perkembangan)

        tv_judul.setText(judul)
        tv_desc.setText(desc)
        rv_1.layoutManager = LinearLayoutManager(this)

        val ref     = "md>>" + id.toString()
        val parcel  = encryption().encob64(ref)

        btn_kuisoner.setOnClickListener {
            val intent = Intent(this@Materi_Detail, Kuisoner::class.java).apply {
                putExtra("mode","PostTest")
                putExtra("m_id",id)
                putExtra("m_judul",judul)
            }
            startActivity(intent)
        }

        btn_perkembangan.setOnClickListener {
            val intent = Intent(this@Materi_Detail, Perkembangan::class.java).apply {
                putExtra("m_id",id)
                putExtra("m_judul",judul)
            }
            startActivity(intent)
        }

        apis.getMateriDetail(parcel).enqueue(object : Callback<List<materi_detail_list>>{
            override fun onResponse(
                call: Call<List<materi_detail_list>?>,
                response: Response<List<materi_detail_list>?>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    if(data != null){
                        adapter = adapter_mater_detail(data[0].res)
                        rv_1.adapter =adapter
                    }
                }
            }

            override fun onFailure(
                call: Call<List<materi_detail_list>?>,
                t: Throwable
            ) {
                Toast.makeText(this@Materi_Detail,"Error => ${t.message.toString()}", Toast.LENGTH_LONG).show()
            }

        })

    }
}