package com.example.imagery_vr.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_materi.ViewHolder
import com.example.imagery_vr.models.materi_detail_items
import com.example.imagery_vr.models.materi_detail_list
import com.example.imagery_vr.ui.perangkat_data
import com.example.imagery_vr.support.deviceData
import com.example.imagery_vr.support.deviceSessionManager
import com.example.imagery_vr.ui.Bluetooth_adapter
import com.example.imagery_vr.ui.Materi_Detail
import com.example.imagery_vr.ui.Materi_Play_Video
import com.example.imagery_vr.ui.Materi_Play_audio
import com.example.imagery_vr.ui.Materi_Play_teks
import com.example.imagery_vr.ui.Profile

class adapter_mater_detail(
    private val data : List<materi_detail_items>
) : RecyclerView.Adapter<adapter_mater_detail.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val card        : CardView  = itemView.findViewById(R.id.cmd_card)
        val tv_judul    : TextView  = itemView.findViewById(R.id.cmd_tv_judul)
        val tv_desc     : TextView  = itemView.findViewById(R.id.cmd_tv_desc)
        val btn_cek     : Button    = itemView.findViewById(R.id.cmd_btn_cek)
        val btn_mulai   : Button    = itemView.findViewById(R.id.cmd_btn_mulai)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_materi_detail,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val dInfo = deviceSessionManager.currentDevice
        val item = data[position]
        holder.tv_judul.text    = item.judul
        holder.tv_desc.text     = item.deskripsi

        val dialog_konfirmasi   = AlertDialog.Builder(holder.itemView.context)
        var intent              = Intent(holder.itemView.context, Bluetooth_adapter::class.java)

        val intentMateriVideo   = Intent(holder.itemView.context, Materi_Play_Video::class.java)
        val intentMateriTeks    = Intent(holder.itemView.context, Materi_Play_teks::class.java)
        val intentMateriAudio   = Intent(holder.itemView.context, Materi_Play_audio::class.java)

        val intentKeBluetooth   = Intent(holder.itemView.context, Bluetooth_adapter::class.java)
        val intentkeData        = Intent(holder.itemView.context, perangkat_data::class.java)

        holder.btn_cek.setOnClickListener {
            pergiKe(holder,intentkeData,position,false,0)
        }

        //Toast.makeText(holder.itemView.context,"Perangkat ${dInfo?.name}", Toast.LENGTH_SHORT).show()

        holder.btn_mulai.setOnClickListener {
            if (item.tipe == "video"){
                if(dInfo?.name == null){
                    dialog_konfirmasi.setMessage("Apakah Anda Ingin Menghubungkna ke Perangkat Tambahan?")
                    dialog_konfirmasi.setTitle("Informasi!")
                    dialog_konfirmasi.setPositiveButton("Ya"){ dialog, which ->
                        pergiKe(holder,intentKeBluetooth,position,true,1)
                    }
                    dialog_konfirmasi.setNegativeButton("Tidak"){ dialog, which ->
                        pergiKe(holder,intentMateriVideo,position,false,1)
                    }
                    val cdialog = dialog_konfirmasi.create()
                    cdialog.show()
                }else{
                    pergiKe(holder,intentMateriVideo,position,false,1)
                }
            }else if (item.tipe == "teks"){
                pergiKe(holder,intentMateriTeks,position,false,2)
            }else if (item.tipe == "audio"){
                if(dInfo?.name == null){
                    dialog_konfirmasi.setMessage("Apakah Anda Ingin Menghubungkna ke Perangkat Tambahan?")
                    dialog_konfirmasi.setTitle("Informasi!")
                    dialog_konfirmasi.setPositiveButton("Ya"){ dialog, which ->
                        pergiKe(holder,intentKeBluetooth,position,true,3)
                    }
                    dialog_konfirmasi.setNegativeButton("Tidak"){ dialog, which ->
                        pergiKe(holder,intentMateriAudio,position,false,3)
                    }
                    val cdialog = dialog_konfirmasi.create()
                    cdialog.show()
                }else{
                    pergiKe(holder,intentMateriAudio,position,false,3)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun pergiKe(holder : ViewHolder ,intent : Intent, pos : Int, device_con : Boolean,tipe : Int){
        val item = data[pos]
        holder.itemView.context.startActivity(intent.apply {
            putExtra("md2_id",item.id)
            putExtra("md2_judul",item.judul)
            putExtra("md2_desc",item.deskripsi)
            putExtra("md2_isi",item.isi)
            putExtra("md2_device",device_con)
            putExtra("md2_tipe",tipe)
        })
    }
}