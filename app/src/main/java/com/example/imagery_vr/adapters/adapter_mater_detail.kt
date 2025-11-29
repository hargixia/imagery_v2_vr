package com.example.imagery_vr.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_materi.ViewHolder
import com.example.imagery_vr.models.materi_detail_items
import com.example.imagery_vr.models.materi_detail_list
import com.example.imagery_vr.ui.Materi_Detail
import com.example.imagery_vr.ui.Materi_Play_Video
import com.example.imagery_vr.ui.Materi_Play_teks

class adapter_mater_detail(
    private val data : List<materi_detail_items>
) : RecyclerView.Adapter<adapter_mater_detail.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val card        : CardView = itemView.findViewById(R.id.cmd_card)
        val tv_judul    : TextView = itemView.findViewById(R.id.cmd_tv_judul)
        val tv_desc     : TextView = itemView.findViewById(R.id.cmd_tv_desc)
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
        val item = data[position]
        holder.tv_judul.text    = item.judul
        holder.tv_desc.text     = item.deskripsi
        holder.card.setOnClickListener {
            if (item.tipe == "video"){
                val intent = Intent(holder.itemView.context, Materi_Play_Video::class.java)
                    .apply {
                        putExtra("md2_id",item.id)
                        putExtra("md2_desc",item.judul)
                        putExtra("md2_video",item.isi)
                    }
                holder.itemView.context.startActivity(intent)
            }else if (item.tipe == "teks"){
            val intent = Intent(holder.itemView.context, Materi_Play_teks::class.java)
                .apply {
                    putExtra("md2_judul",item.judul)
                    putExtra("md2_desc",item.deskripsi)
                    putExtra("md2_isi",item.isi)
                }
            holder.itemView.context.startActivity(intent)
        }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}