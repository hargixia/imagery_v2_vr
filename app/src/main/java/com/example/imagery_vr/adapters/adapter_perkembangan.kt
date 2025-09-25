package com.example.imagery_vr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_materi.ViewHolder
import com.example.imagery_vr.models.perkembangan_data_list
import com.example.imagery_vr.models.perkembangan_res

class adapter_perkembangan(
    private val dataList    : List<perkembangan_data_list>,
) : RecyclerView.Adapter<adapter_perkembangan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_no       : TextView  = itemView.findViewById(R.id.scp_no)
        val tv_1        : TextView  = itemView.findViewById(R.id.scp_text_1)
        val tv_2        : TextView  = itemView.findViewById(R.id.scp_text_2)
        val tv_3        : TextView  = itemView.findViewById(R.id.scp_text_3)
        val tv_4        : TextView  = itemView.findViewById(R.id.scp_text_4)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_perkembangan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = dataList[position]
        holder.tv_no.text   = item.id.toString()
        holder.tv_1.text    = item.nilai.toString()
        holder.tv_2.text    = item.kategori.toString()
        holder.tv_3.text    = item.hari
        holder.tv_4.text    = item.waktu
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}