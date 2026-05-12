package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.imagery_vr.R
import com.example.imagery_vr.models.perangkat_perkembangan_req
import com.example.imagery_vr.models.perangkat_perkembangan_res
import com.example.imagery_vr.models.perkembangan_res
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.deviceSessionManager
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.response
import com.example.imagery_vr.support.retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Profile : AppCompatActivity() {

    private lateinit var tv1        : TextView
    private lateinit var tv2        : TextView
    private lateinit var tv3        : TextView
    private lateinit var tv4        : TextView

    val dataParcel                  : Array<String?> = arrayOfNulls(3)
    private val bluetoothAdapter    : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket     : BluetoothSocket? = null
    private val uuid                : UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private lateinit var ds         : SharedPreferences
    private var user_id             : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dInfo       = deviceSessionManager.currentDevice
        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id         = ds.getInt("user_id",0)

        tv1 = findViewById<TextView>(R.id.profil_tv1)
        tv2 = findViewById<TextView>(R.id.profil_tv2)
        tv3 = findViewById<TextView>(R.id.profil_tv3)
        tv4 = findViewById<TextView>(R.id.profil_tv4)

        tv1.text = "Nama Perangkat :${dInfo?.name}"
        connectToDevice(dInfo?.cdevice)
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice?) {
        // Menjalankan koneksi di background thread agar UI tidak macet
        Thread {
            try {
                bluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)
                bluetoothAdapter?.cancelDiscovery() // Hentikan pencarian sebelum koneksi
                bluetoothSocket?.connect()

                runOnUiThread {
                    Toast.makeText(this, "Terhubung ke ${device?.name}", Toast.LENGTH_SHORT).show()
                }

                receiveData(bluetoothSocket)

            } catch (e: IOException) {
                e.printStackTrace()
                deviceSessionManager.connected = false
                runOnUiThread {
                    Toast.makeText(this, "Koneksi Gagal", Toast.LENGTH_SHORT).show()
                }
                try {
                    bluetoothSocket?.close()
                } catch (closeException: IOException) { }
            }
        }.start()
    }

    private fun receiveData(socket: BluetoothSocket?) {
        val inputStream     = socket?.inputStream
        val reader          = inputStream?.bufferedReader()

        var dataCollection  = ArrayList<String>()
        var req             = "ppi>>" + "1>>" + user_id.toString()
        var collection      = ""

        val apis            = retrofit.instance.create(api_services::class.java)

        var pos             = 0
        val holder          = 10
        var unsent          = 0
        var sent            = 0

        val format_jam      = DateTimeFormatter.ofPattern("HH:mm:ss")
        val format_tgl      = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        while (socket != null && socket.isConnected) {
            try {

                val incomingText = reader?.readLine()

                if (incomingText != null) {
                    Log.d("pos", "pos : ${pos.toString()}")
                    Log.d("unsent", "unsent : ${unsent.toString()}")
                    Log.d("sent", "sent : ${sent.toString()}")

                    if (dataCollection.size > holder) {
                        Log.d("process", "Sending....")

                        val data = perangkat_perkembangan_req(
                            encryption().encob64(req),
                            encryption().encob64(collection)
                        )

                        // Kirim data secara sinkron atau asinkron tanpa coroutine berlebih
                        apis.getPerkembangan_perangkatIN(data).enqueue(object : Callback<perangkat_perkembangan_res> {
                            override fun onResponse(
                                p0: Call<perangkat_perkembangan_res?>,
                                p1: Response<perangkat_perkembangan_res?>
                            ) {
                                if (p1.isSuccessful) {
                                    if(p1.body()?.code == 1){
                                        Log.d("success", "complete : ${p1.body()?.code}")
                                        sent += 1
                                    }else{
                                        Log.e("unsuccess", "Error un : ${p1.body()?.code}")
                                        unsent += 1
                                    }
                                } else {
                                    Log.e("unsuccess", "Error un : ${p1.code().toString()}")
                                    unsent += 1
                                }

                            }

                            override fun onFailure(
                                p0: Call<perangkat_perkembangan_res?>,
                                p1: Throwable
                            ) {
                                Log.e("unsuccess", "Error onFailure : ${p1.message.toString()}")
                                unsent += 1
                            }
                        })

                        pos = -1
                        dataCollection.clear()
                        collection = ""
                        Log.d("process", "Data Cleared")
                    } else {
                        val waktu_sekarang  = LocalDateTime.now()
                        var jam_c           = waktu_sekarang.format(format_jam)
                        var tgl_c           = waktu_sekarang.format(format_tgl)
                        var finalData       = incomingText + ">>" + jam_c + ">>" + tgl_c
                        val enco            = encryption().encob64(finalData)
                        tv2.text = incomingText
                        tv3.text = finalData
                        dataCollection.add(enco)
                        collection += enco
                        if (pos <= (holder - 1)) {
                            collection += ">>"
                        }
                    }
                    pos += 1
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break // Keluar dari loop jika koneksi terputus
            }
        }
    }
}