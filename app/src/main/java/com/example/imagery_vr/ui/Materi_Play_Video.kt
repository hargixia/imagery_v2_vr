package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.imagery_vr.R
import com.example.imagery_vr.support.deviceData
import com.example.imagery_vr.support.deviceSessionManager
import java.io.IOException
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

    // UUID Standar SPP
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null


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
                disconnectToDevice()
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
                runOnUiThread {
                    Toast.makeText(this, "Koneksi Gagal", Toast.LENGTH_SHORT).show()
                }
                try {
                    bluetoothSocket?.close()
                } catch (closeException: IOException) { }
            }
        }.start()
    }

    @SuppressLint("MissingPermission")
    private fun disconnectToDevice() {
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

    private fun receiveData(socket: BluetoothSocket?) {
        val inputStream = socket?.inputStream
        val reader = inputStream?.bufferedReader()

        while (socket != null && socket.isConnected) {
            try {
                val incomingText = reader?.readLine()
                if (incomingText != null) {
                    // Update UI harus dilakukan di Main Thread
                    runOnUiThread {
                        Toast.makeText(this,"Data : ${incomingText}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break // Keluar dari loop jika koneksi terputus
            }
        }
    }

}