package com.example.imagery_vr.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import com.example.imagery_vr.support.deviceSessionManager
import com.example.imagery_vr.support.encryption
import java.io.IOException
import java.util.UUID

class Profile : AppCompatActivity() {

    private lateinit var tv1        : TextView
    private lateinit var tv2        : TextView
    private lateinit var tv3        : TextView

    val dataParcel                  : Array<String?> = arrayOfNulls(3)
    private val bluetoothAdapter    : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket     : BluetoothSocket? = null
    private val uuid                : UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dInfo = deviceSessionManager.currentDevice
        tv1 = findViewById<TextView>(R.id.profil_tv1)
        tv2 = findViewById<TextView>(R.id.profil_tv2)
        tv3 = findViewById<TextView>(R.id.profil_tv3)

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

        while (socket != null && socket.isConnected) {
            try {
                val incomingText = reader?.readLine()
                if (incomingText != null) {
                    // Update UI harus dilakukan di Main Thread
                    runOnUiThread {
                        tv2.text = incomingText
                        dataParcel[0] = encryption().splitter(incomingText)[0]
                        dataParcel[1] = encryption().splitter(incomingText)[1]
                        dataParcel[2] = encryption().splitter(incomingText)[2]
                        tv3.text = dataParcel[0]
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break // Keluar dari loop jika koneksi terputus
            }
        }
    }
}