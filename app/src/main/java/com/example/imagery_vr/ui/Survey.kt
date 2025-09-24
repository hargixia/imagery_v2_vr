package com.example.imagery_vr.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_survey
import com.example.imagery_vr.models.survey_jawaban
import com.example.imagery_vr.models.survey_soal
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Survey : AppCompatActivity() {

    private lateinit var adapter        : adapter_survey
    private lateinit var rv_1           : RecyclerView
    private lateinit var tx_1           : TextView
    private lateinit var ds             : SharedPreferences
    private var s_jawaban               : List<survey_jawaban> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_survey)

        val apis = retrofit.instance.create(api_services::class.java)

        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        val ds_nama     = ds.getString("nama","user")
        val ds_id       = ds.getInt("user_id",0)

        rv_1 = findViewById(R.id.survey_rv_1)
        rv_1.layoutManager = LinearLayoutManager(this)

        tx_1 = findViewById(R.id.survey_tx_nama)
        tx_1.text = "Nama : $ds_nama"

        apis.getSurveyPertanyaan().enqueue(object : Callback<List<survey_soal>>{
            override fun onResponse(
                call: Call<List<survey_soal>?>,
                response: Response<List<survey_soal>?>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    if(data != null){
                        adapter = adapter_survey(data, ds_id){jawabans ->
                            s_jawaban = jawabans
                        }
                        rv_1.adapter = adapter
                    }
                }
            }

            override fun onFailure(
                call: Call<List<survey_soal>?>,
                t: Throwable
            ) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}