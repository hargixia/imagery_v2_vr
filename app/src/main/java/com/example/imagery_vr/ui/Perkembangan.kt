package com.example.imagery_vr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_perkembangan
import com.example.imagery_vr.models.perkembangan_res
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Perkembangan : AppCompatActivity() {


    private lateinit var t_judul            : TextView
    private lateinit var t_nama             : TextView
    private lateinit var t_umur             : TextView
    private lateinit var t_jk               : TextView
    private lateinit var t_jdata            : TextView
    private lateinit var t_max              : TextView
    private lateinit var t_min              : TextView
    private lateinit var t_last             : TextView
    private lateinit var t_avg              : TextView
    private lateinit var t_kat              : TextView

    private lateinit var t_userh_n          : TextView
    private lateinit var t_userh_v          : TextView

    private lateinit var t_status           : TextView

    private lateinit var grafik1            : LineChart
    private lateinit var rv1                : RecyclerView
    private lateinit var adapter            : adapter_perkembangan
    private lateinit var ds                 : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perkembangan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apis            = retrofit.instance.create(api_services::class.java)
        ds                  = getSharedPreferences("IMGV1",MODE_PRIVATE)

        val user_id         = ds.getInt("user_id",0)
        val materi_id       = intent.getIntExtra("m_id",0)

        t_judul             = findViewById(R.id.p_judul)
        t_nama              = findViewById(R.id.p_nama)
        t_umur              = findViewById(R.id.p_umur)
        t_jk                = findViewById(R.id.p_gender)

        t_jdata             = findViewById(R.id.p_ctest)
        t_max               = findViewById(R.id.p_maxTest)
        t_min               = findViewById(R.id.p_minTest)
        t_avg               = findViewById(R.id.p_avgTest)
        t_kat               = findViewById(R.id.p_kategoriTest)
        t_last              = findViewById(R.id.p_lastTest)

        t_userh_n           = findViewById(R.id.p_u_high_nama)
        t_userh_v           = findViewById(R.id.p_u_high_val)

        t_status            = findViewById(R.id.p_status)

        grafik1             = findViewById(R.id.p_chart1)

        rv1                 = findViewById(R.id.p_rv1)
        rv1.layoutManager   = LinearLayoutManager(this)

        val str     = "pu>>" + user_id.toString() + ">>" + materi_id.toString()
        val parcel  = encryption().encob64(str)
        apis.getPerkembangan(parcel).enqueue(object : Callback<List<perkembangan_res>>{
            override fun onResponse(
                call: Call<List<perkembangan_res>?>,
                response: Response<List<perkembangan_res>?>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        if (data[0].code == 200) {

                            val datas : ArrayList<Entry> = ArrayList()
                            var i = 1
                            for(dp in data[0].res[0].data){
                                datas.add(Entry(i.toFloat(),dp.nilai))
                                i += 1
                            }
                            val dataSet = LineDataSet(datas,"Perkembangan Dirimu")
                            dataSet.color = Color.BLUE
                            dataSet.setCircleColor(Color.BLUE)
                            dataSet.setDrawValues(false)
                            dataSet.lineWidth = 3f

                            val lineData = LineData(dataSet)
                            grafik1.data = lineData
                            grafik1.description.isEnabled = false // Hide description label
                            grafik1.setTouchEnabled(true)
                            grafik1.isDragEnabled = true
                            grafik1.setScaleEnabled(true)
                            grafik1.setPinchZoom(true)
                            grafik1.invalidate()

                            adapter = adapter_perkembangan(data[0].res[0].data)
                            rv1.adapter = adapter

                            t_judul.text    = data[0].res[0].judul
                            t_nama.text     = ": " + data[0].res[0].nama
                            t_umur.text     = ": " + data[0].res[0].umur + " Tahun"
                            t_jk.text       = ": " + data[0].res[0].gender

                            t_jdata.text    = ": " + "${data[0].res[0].t_jumlah.toInt().toString()}X Test"
                            t_max.text      = ": " + data[0].res[0].t_max.toString()
                            t_min.text      = ": " + data[0].res[0].t_min.toString()
                            t_avg.text      = ": " + data[0].res[0].t_avg.toString()
                            t_kat.text      = ": " + data[0].res[0].kategori.toString()
                            t_last.text     = ": " + "${data[0].res[0].last_h}, ${data[0].res[0].last_w}"

                            t_userh_n.text  = ": " + data[0].res[0].utinggi
                            t_userh_v.text  = ": " + data[0].res[0].tinggi.toString()

                            t_status.text   = ": " + data[0].res[0].status

                        } else if (data[0].code == 404) {
                            startActivity(Intent(this@Perkembangan, materi::class.java))
                            Toast.makeText(this@Perkembangan, "Tidak Ada Data.", Toast.LENGTH_LONG)
                                .show()
                        } else if (data[0].code == 405) {
                            startActivity(Intent(this@Perkembangan, materi::class.java))
                            Toast.makeText(
                                this@Perkembangan,
                                "Kesalahan Bidang.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Perkembangan,
                            "Error Server Response.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(
                call: Call<List<perkembangan_res>?>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@Perkembangan,
                    "Error: ${t.message.toString()}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }
}