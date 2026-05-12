package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.perangkat_perkembangan_req
import com.example.imagery_vr.models.perangkat_perkembangan_res
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.deviceData
import com.example.imagery_vr.support.deviceSessionManager
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Materi_Play_Video : AppCompatActivity() {

    private lateinit var tv_desc        : TextView
    private lateinit var tv_count_down  : TextView
    private lateinit var tv1            : TextView
    private lateinit var videoplayer    : PlayerView
    private lateinit var exoplayer      : ExoPlayer

    lateinit var mediaControl           : android.widget.MediaController
    private lateinit var mediasesion    : androidx.media3.session.MediaSession

    private var countDownTimer: CountDownTimer? = null

    val dataParcel                  : Array<String?> = arrayOfNulls(3)
    private val bluetoothAdapter    : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket     : BluetoothSocket? = null
    private val uuid                : UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private lateinit var ds         : SharedPreferences
    private var user_id             : Int = 0

    val dInfo                       = deviceSessionManager.currentDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val desc        = intent.getStringExtra("md2_desc")
        val video_url   = intent.getStringExtra("md2_video")

        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id         = ds.getInt("user_id",0)

        exoplayer = ExoPlayer.Builder(this).build()
        mediasesion = androidx.media3.session.MediaSession.Builder(this,exoplayer).build()

        tv_desc         = findViewById(R.id.m_pv_tv1)
        tv_count_down   = findViewById(R.id.m_pv_countdown)
        tv1             = findViewById(R.id.m_pv_tv1)
        videoplayer     = findViewById(R.id.m_pv_videoplayer)

        tv_desc.setText(desc)
        videoplayer.player = exoplayer
        val url_parse = Uri.parse(video_url)
        val mediaItem = MediaItem.fromUri(url_parse)
        exoplayer.setMediaItem(mediaItem)
        exoplayer.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        exoplayer.prepare()

        val device = deviceSessionManager

        if(device.connected == true){
            Toast.makeText(this,"device connected", Toast.LENGTH_SHORT).show()
            val currentDevice = device.currentDevice
            connectToDevice(currentDevice?.cdevice)
        }else{
            Toast.makeText(this,"device not coneected", Toast.LENGTH_SHORT).show()
        }

        startCountdown(11000,1000)

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                exoplayer.stop()
                videoplayer.player?.stop()
                mediasesion.release()
                disconnectDevice()
                finish()
                Toast.makeText(this@Materi_Play_Video,"Video Selesai", Toast.LENGTH_SHORT).show()
            }
        }
        onBackPressedDispatcher.addCallback(this@Materi_Play_Video,callback)
    }

    private fun startCountdown(totalTimeMillis: Long, intervalMillis: Long) {
        countDownTimer?.cancel() // Cancel any existing timer

        countDownTimer = object : CountDownTimer(totalTimeMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tv_count_down.text = " $secondsLeft"
            }

            override fun onFinish() {
                tv_desc.visibility          = View.GONE
                tv_count_down.visibility    = View.GONE
                tv1.visibility              = View.GONE
                val vp_layout               = videoplayer.layoutParams as LinearLayout.LayoutParams
                vp_layout.height = ViewGroup.LayoutParams.MATCH_PARENT
                videoplayer.layoutParams = vp_layout
                videoplayer.player?.play()
                connectToDevice(dInfo?.cdevice)

                WindowCompat.setDecorFitsSystemWindows(window,false)
                WindowInsetsControllerCompat(window,window.decorView).let { controller->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }.start()
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

    private fun disconnectDevice() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket?.close()
                bluetoothSocket = null // Kosongkan variabel setelah ditutup

                Toast.makeText(this, "Koneksi diputus", Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Gagal memutus koneksi", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tidak ada koneksi yang aktif", Toast.LENGTH_SHORT).show()
        }
    }

}