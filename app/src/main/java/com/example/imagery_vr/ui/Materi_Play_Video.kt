package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
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
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.perangkat_akses_catat_req
import com.example.imagery_vr.models.perangkat_akses_catat_res
import com.example.imagery_vr.models.perangkat_in_req
import com.example.imagery_vr.models.perangkat_in_res
import com.example.imagery_vr.support.api_services
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

    private lateinit var ds         : SharedPreferences
    private var user_id             : Int = 0

    val apis                        = retrofit.instance.create(api_services::class.java)

    val dInfo                       = deviceSessionManager.currentDevice
    var device_con                  : Boolean = false
    var idAccess                    : Int = 0
    var ida                         : Int = 0

    val format_jam                  = DateTimeFormatter.ofPattern("HH:mm:ss")
    val format_tgl                  = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val SERVICE_UUID: UUID = UUID.fromString("dc38bbe4-d0f5-4d29-8cb3-004a9efeef64")
    val CHAR_UUID: UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
    private val DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    var bluetoothGatt: BluetoothGatt? = null

    var collection      = ""
    var dataCollection  = ArrayList<String>()
    var pos             = 0
    val holder          = 10
    var unsent          = 0
    var sent            = 0

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idm         = intent.getIntExtra("md2_id",0)
        val desc        = intent.getStringExtra("md2_desc")
        val video_url   = intent.getStringExtra("md2_isi")
        device_con      = intent.getBooleanExtra("md2_device",false)

        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id         = ds.getInt("user_id",0)

        exoplayer = ExoPlayer.Builder(this).build()
        mediasesion = androidx.media3.session.MediaSession.Builder(this,exoplayer).build()

        tv_desc         = findViewById(R.id.m_pv_desc)
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
        exoplayer.preloadConfiguration = ExoPlayer.PreloadConfiguration(3_000_000L)

        val device = deviceSessionManager

        if (device_con == true){
            if(device.connected == true){

                val currentDevice = device.currentDevice
                Toast.makeText(this,"device : ${currentDevice?.cdevice}", Toast.LENGTH_SHORT).show()
                connectToBLEDevice(currentDevice?.cdevice)
                //connectToDevice(currentDevice?.cdevice)

                val mode        = encryption().encob64("materi_detail_access")
                val eidm        = encryption().encob64(idm.toString())
                val idu         = encryption().encob64(user_id.toString())
                val perangkat   = device_con.toString() + ">>" + currentDevice?.name
                val ep          = encryption().encob64(perangkat)

                val parcel = perangkat_akses_catat_req(
                    mode,
                    ep,
                    eidm,
                    idu
                )

                apis.catatAkses(parcel).enqueue(object : Callback<perangkat_akses_catat_res> {
                    override fun onResponse(
                        p0: Call<perangkat_akses_catat_res?>,
                        p1: Response<perangkat_akses_catat_res?>
                    ) {
                        if (p1.isSuccessful){
                            val data = p1.body()
                            if(data != null && data.code == 1){
                                idAccess = data.res[0].id
                                Log.d("Access Created","ID Access : ${idAccess.toString()}")
                            }else{
                                Log.d("Access Not Created","Code : ${data?.code}")
                            }
                        }else{
                            Log.d("Access Not Created","Unsuccess")
                        }

                    }

                    override fun onFailure(
                        p0: Call<perangkat_akses_catat_res?>,
                        p1: Throwable
                    ) {
                        Log.e("Access Not Created", "Error : ${p1.message}")
                    }
                })

            }else{
                //Toast.makeText(this,"device not connected", Toast.LENGTH_SHORT).show()
                hapusAksi()
            }
        }

        //Toast.makeText(this,"Mode Device = ${device_con}", Toast.LENGTH_LONG).show()

        startCountdown(11000,1000)
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                hapusAksi()
            }
        }

        onBackPressedDispatcher.addCallback(this@Materi_Play_Video,callback)
    }

    fun hapusAksi(){
        exoplayer.stop()
        videoplayer.player?.stop()
        mediasesion.release()
        disconnectBLE()
        finish()
        deviceSessionManager.clearData()
        Toast.makeText(this@Materi_Play_Video,"Video Selesai", Toast.LENGTH_SHORT).show()
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
    private fun connectToBLEDevice(device: BluetoothDevice?) {
        Toast.makeText(this, "Menghubungkan ke ${device?.name}...", Toast.LENGTH_SHORT).show()
        // Putuskan koneksi sebelumnya jika ada
        disconnectBLE()
        // Mulai koneksi GATT
        bluetoothGatt = device?.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread { Toast.makeText(this@Materi_Play_Video, "Perangkat Terhubung!", Toast.LENGTH_SHORT).show() }
                Log.d("BLE_GATT", "Terhubung. Mencari Services...")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gagalTerhubung()
            }else{
                gagalTerhubung()
            }
        }

        fun gagalTerhubung(){
            runOnUiThread { Toast.makeText(this@Materi_Play_Video, "Perangkat Terputus", Toast.LENGTH_SHORT).show() }
            Log.d("BLE_GATT", "Terputus dari perangkat.")
            bluetoothGatt?.close()
            bluetoothGatt = null
            finish()
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHAR_UUID)

                if (characteristic != null) {
                    // Mengaktifkan fitur "Notify" agar Android menerima data otomatis tanpa harus bertanya
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID)
                    if (descriptor != null) {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                    Log.d("BLE_GATT", "Service dan Characteristic ditemukan. Notify diaktifkan.")
                } else {
                    Log.e("BLE_GATT", "Characteristic tidak ditemukan!")
                }
            }
        }

        // Fungsi ini dipanggil otomatis setiap kali ESP32 mengirim data baru (Notify)
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // Membaca data dalam format String
            val data = characteristic.getStringValue(0)
            var req  = "ppi>>" + idAccess.toString() + ">>" + user_id.toString()

            Log.d("BLE_DATA", "Data masuk: $data")

            try {

                if (data != null && device_con == true) {
                    runOnUiThread {
                        Log.d("pos", "pos : ${pos.toString()}")
                        Log.d("unsent", "unsent : ${unsent.toString()}")
                        Log.d("sent", "sent : ${sent.toString()}")

                        if (dataCollection.size > holder) {
                            Log.d("process", "Sending....")

                            val data = perangkat_in_req(
                                encryption().encob64(req),
                                encryption().encob64(collection)
                            )

                            // Kirim data secara sinkron atau asinkron tanpa coroutine berlebih
                            apis.postPerkembangan_perangkatIN(data).enqueue(object : Callback<perangkat_in_res> {
                                override fun onResponse(
                                    p0: Call<perangkat_in_res?>,
                                    p1: Response<perangkat_in_res?>
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
                                    p0: Call<perangkat_in_res?>,
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
                            var finalData       = data + ">>" + jam_c + ">>" + tgl_c
                            val enco            = encryption().encob64(finalData)

                            dataCollection.add(enco)
                            collection += enco
                            if (pos <= (holder - 1)) {
                                collection += ">>"
                            }
                        }
                        pos += 1
                        //Toast.makeText(this,"pos : ${pos} | sent : ${sent} | unsent : ${unsent}", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@Materi_Play_Video,"no data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // --- BAGIAN 3: PEMUTUSAN KONEKSI ---

    @SuppressLint("MissingPermission")
    private fun disconnectBLE() {
        if (bluetoothGatt != null) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
            deviceSessionManager.clearData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hapusAksi()
    }

}