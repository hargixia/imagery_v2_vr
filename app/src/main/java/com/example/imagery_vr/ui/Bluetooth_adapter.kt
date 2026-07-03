package com.example.imagery_vr.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import com.example.imagery_vr.support.deviceData
import com.example.imagery_vr.support.deviceSessionManager
import java.io.IOException
import java.util.UUID
import kotlin.math.log

class Bluetooth_adapter : AppCompatActivity() {

    private lateinit var spinnerDevices: Spinner
    private lateinit var btnConnect: Button
    private lateinit var tvIncomingData: TextView

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val deviceList = ArrayList<BluetoothDevice>()
    private val deviceNames = ArrayList<String>()

    var md2_id  : Int = 0
    var md2_judul : String? = ""
    var md2_desc : String? = ""
    var md2_isi : String? = ""
    var md2_device : Boolean = false

    var md2_tipe : Int = 0

    // UUID Standar SPP
    // UUID yang sama dengan ESP32
    val SERVICE_UUID: UUID = UUID.fromString("dc38bbe4-d0f5-4d29-8cb3-004a9efeef64")
    val CHAR_UUID: UUID = UUID.fromString("23677c9f-9394-4de3-87b2-7b0c158a4c02")
    private var bluetoothSocket: BluetoothSocket? = null

    var bluetoothGatt: BluetoothGatt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bluetooth_adapter)

        // Inisialisasi UI
        spinnerDevices = findViewById(R.id.spinnerDevices)
        btnConnect = findViewById(R.id.btnConnect)
        tvIncomingData = findViewById(R.id.tvIncomingData)

        scanActiveDevices()
        //loadPairedDevices()
        //val devi = deviceSessionManager.currentDevice
        //Toast.makeText(this,"val : ${devi?.name}", Toast.LENGTH_SHORT).show()

        md2_id = intent.getIntExtra("md2_id",0)
        md2_judul = intent.getStringExtra("md2_judul")
        md2_desc = intent.getStringExtra("md2_desc")
        md2_isi = intent.getStringExtra("md2_isi")
        md2_device = true
        md2_tipe = intent.getIntExtra("md2_tipe",0)

        btnConnect.setOnClickListener {
            val selectedPosition = spinnerDevices.selectedItemPosition
            if (selectedPosition != -1 && deviceList.isNotEmpty()) {
                val selectedDevice = deviceList[selectedPosition]
                //connectToDevice(selectedDevice)
                deviceSessionManager.currentDevice = deviceData(
                    cdevice = selectedDevice,
                    name = selectedDevice.name
                )
                deviceSessionManager.connected = true
                //connectToDevice(selectedDevice)
                var intent : Intent
                when(md2_tipe){
                    1 -> intent = Intent(this@Bluetooth_adapter, Materi_Play_Video::class.java)
                    2 -> intent = Intent(this@Bluetooth_adapter, Materi_Play_teks::class.java)
                    3 -> intent = Intent(this@Bluetooth_adapter, Materi_Play_audio::class.java)
                    else -> intent = Intent(this@Bluetooth_adapter, Dashboard::class.java)
                }
                startActivity(intent.apply {
                    putExtra("md2_id",md2_id)
                    putExtra("md2_judul",md2_judul)
                    putExtra("md2_desc",md2_desc)
                    putExtra("md2_isi",md2_isi)
                    putExtra("md2_device",md2_device)
                })
            } else {
                Toast.makeText(this, "Pilih perangkat terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            Toast.makeText(this@Bluetooth_adapter,"Tidak ada izin bluetooth.", Toast.LENGTH_LONG).show()
        } else {
            scanActiveDevices()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action

            // Jika sebuah perangkat Bluetooth ditemukan
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Ambil objek perangkat dari intent
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                if (device != null) {
                    // Ambil nama perangkat. Jika null, beri nilai string kosong agar tidak error
                    val deviceName = device.name ?: ""
                    val deviceAddress = device.address

                    // --- LOGIKA FILTERING ---
                    // Mengecek apakah nama perangkat diawali dengan "IMAGERY"
                    // parameter ignoreCase = true membuat pencarian kebal terhadap huruf besar/kecil (Imagery, imagery, IMAGERY akan tetap terbaca)
                    if (deviceName.startsWith("IMAGERY", ignoreCase = true)) {

                        // Cegah duplikasi di dalam list
                        if (!deviceList.contains(device)) {
                            deviceList.add(device)
                            deviceNames.add("$deviceName ($deviceAddress)")

                            // Beritahu Spinner bahwa ada data baru yang valid
                            (spinnerDevices.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanActiveDevices() {
        // 1. Bersihkan daftar UI yang lama
        deviceList.clear()
        deviceNames.clear()

        // Set adapter ke Spinner agar tidak kosong saat mulai mencari
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDevices.adapter = adapter

        // 2. Daftarkan BroadcastReceiver untuk menangkap perangkat yang ditemukan
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // 3. Mulai proses pemindaian
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery() // Hentikan pencarian lama jika masih berjalan
        }

        val started = bluetoothAdapter?.startDiscovery()
        if (started == true) {
            Toast.makeText(this, "Mencari perangkat aktif...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal memulai pencarian", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Hentikan proses pemindaian agar hemat baterai
        @SuppressLint("MissingPermission")
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }

        // Cabut pendaftaran BroadcastReceiver
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Blok catch ini berguna jika receiver belum sempat terdaftar tetapi aplikasi sudah ditutup
            e.printStackTrace()
        }
    }

}