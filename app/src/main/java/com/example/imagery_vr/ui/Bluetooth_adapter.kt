package com.example.imagery_vr.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothSocket
import android.content.Intent
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

        loadPairedDevices()
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
            loadPairedDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        if (pairedDevices?.isNotEmpty() == true) {
            for (device in pairedDevices) {
                deviceList.add(device)
                deviceNames.add("${device.name} (${device.address})")
            }

            // Memasukkan daftar nama ke dalam Spinner
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDevices.adapter = adapter
        } else {
            Toast.makeText(this, "Tidak ada perangkat Bluetooth terpasang", Toast.LENGTH_SHORT).show()
        }
    }

}