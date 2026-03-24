package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import java.io.IOException
import java.util.UUID

class Bluetooth_adapter : AppCompatActivity() {

    private lateinit var spinnerDevices: Spinner
    private lateinit var btnConnect: Button
    private lateinit var tvIncomingData: TextView

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val deviceList = ArrayList<BluetoothDevice>()
    private val deviceNames = ArrayList<String>()

    // UUID Standar SPP
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bluetooth_adapter)

        // Inisialisasi UI
        spinnerDevices = findViewById(R.id.spinnerDevices)
        btnConnect = findViewById(R.id.btnConnect)
        tvIncomingData = findViewById(R.id.tvIncomingData)

        loadPairedDevices()

        btnConnect.setOnClickListener {
            val selectedPosition = spinnerDevices.selectedItemPosition
            if (selectedPosition != -1 && deviceList.isNotEmpty()) {
                val selectedDevice = deviceList[selectedPosition]
                connectToDevice(selectedDevice)
            } else {
                Toast.makeText(this, "Pilih perangkat terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
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

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        // Menjalankan koneksi di background thread agar UI tidak macet
        Thread {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothAdapter?.cancelDiscovery() // Hentikan pencarian sebelum koneksi
                bluetoothSocket?.connect()

                runOnUiThread {
                    Toast.makeText(this, "Terhubung ke ${device.name}", Toast.LENGTH_SHORT).show()
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

    private fun receiveData(socket: BluetoothSocket?) {
        val inputStream = socket?.inputStream
        val reader = inputStream?.bufferedReader()

        while (socket != null && socket.isConnected) {
            try {
                val incomingText = reader?.readLine()
                if (incomingText != null) {
                    // Update UI harus dilakukan di Main Thread
                    runOnUiThread {
                        tvIncomingData.text = incomingText
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break // Keluar dari loop jika koneksi terputus
            }
        }
    }

}