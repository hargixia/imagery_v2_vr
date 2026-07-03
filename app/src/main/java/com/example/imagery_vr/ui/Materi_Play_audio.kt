package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class Materi_Play_audio : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    // Ganti dengan URL audio Anda yang sebenarnya
    private var audioUrl = "URL_AUDIO"

    private lateinit var tx_judul       : TextView
    private lateinit var tx_desc        : TextView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_audio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        user_id         = ds.getInt("user_id",0)

        val i_judul     = intent.getStringExtra("md2_judul")
        val i_desc      = intent.getStringExtra("md2_desc")
        val i_isi       = intent.getStringExtra("md2_isi")
        val idm         = intent.getIntExtra("md2_id",0)
        device_con      = intent.getBooleanExtra("md2_device",false)

        tx_judul    = findViewById(R.id.mpa_judul)
        tx_desc     = findViewById(R.id.mpa_deskripsi)

        tx_judul.text   = i_judul
        tx_desc.text    = i_desc
        audioUrl = i_isi.toString()

        val device = deviceSessionManager

        if (device_con == true){
            if(device.connected == true){
                //Toast.makeText(this,"device connected", Toast.LENGTH_SHORT).show()
                val currentDevice = device.currentDevice
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

        startAudioPlaybackAndFinishOnCompletion()
    }

    private fun startAudioPlaybackAndFinishOnCompletion() {
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.apply {
                // Konfigurasi atribut audio
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                // Tetapkan sumber data dari URL
                setDataSource(audioUrl)

                // Listener saat persiapan selesai (siap untuk dimainkan)
                setOnPreparedListener { mp ->
                    mp.start()
                    Toast.makeText(this@Materi_Play_audio, "Memutar Audio...", Toast.LENGTH_SHORT).show()
                }

                // *** Kunci Implementasi: Selesai lalu Tutup Activity ***
                // Listener saat pemutaran selesai
                setOnCompletionListener {
                    // Lepaskan sumber daya MediaPlayer
                    releaseMediaPlayer()

                    // Tutup Activity (berpindah kembali ke Activity sebelumnya)
                    finish()
                }

                // Listener jika terjadi error
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "Error saat pemutaran: $what, $extra")
                    Toast.makeText(this@Materi_Play_audio, "Gagal memutar audio.", Toast.LENGTH_LONG).show()
                    releaseMediaPlayer()
                    finish()
                    true // Mengembalikan true menunjukkan error telah ditangani
                }

                // Persiapan Asinkron (penting untuk URL/streaming)
                prepareAsync()
            }

        } catch (e: Exception) {
            Log.e("AudioPlayer", "Exception: ${e.message}")
            e.printStackTrace()
            releaseMediaPlayer()
            finish()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop() // Hentikan jika masih memutar
            }
            release() // Lepaskan sumber daya
        }
        mediaPlayer = null
    }

    fun hapusAksi(){
        disconnectBLE()
        finish()
        deviceSessionManager.clearData()
        Toast.makeText(this@Materi_Play_audio,"Video Selesai", Toast.LENGTH_SHORT).show()
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
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@Materi_Play_audio, "BLE Terhubung!", Toast.LENGTH_SHORT).show() }
                Log.d("BLE_GATT", "Terhubung. Mencari Services...")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@Materi_Play_audio, "BLE Terputus", Toast.LENGTH_SHORT).show() }
                Log.d("BLE_GATT", "Terputus dari perangkat.")
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
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


            var req             = "ppi>>" + idAccess.toString() + ">>" + user_id.toString()

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
                    Toast.makeText(this@Materi_Play_audio,"no data", Toast.LENGTH_SHORT).show()
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
        releaseMediaPlayer()
        disconnectBLE() // Mencegah memory leak saat aplikasi ditutup
    }
}