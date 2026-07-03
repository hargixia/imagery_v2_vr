package com.example.imagery_vr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.imagery_vr.R
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.models.perangkat_data_list

class adapter_perangkat_data2 (
    private val dataList: List<perangkat_data_list>
) : RecyclerView.Adapter<adapter_perangkat_data2.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_pos          : TextView = itemView.findViewById(R.id.cpd2_pos)
        val tv_bpm          : TextView = itemView.findViewById(R.id.cpd2_tv_bpm)
        val tv_gsr          : TextView = itemView.findViewById(R.id.cpd2_tv_gsr)
        val tv_suhu         : TextView = itemView.findViewById(R.id.cpd2_tv_suhu)
        val tv_tanggal      : TextView = itemView.findViewById(R.id.cpd2_tv_tanggal)
        val tv_waktu        : TextView = itemView.findViewById(R.id.cpd2_tv_waktu)
    }

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_item_perangkat_data2,p0,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        p0: ViewHolder,
        p1: Int
    ) {
        val item = dataList[p1]
        p0.tv_pos.text = p1.toString()
        p0.tv_bpm.text = item.bpm.toString()
        p0.tv_gsr.text = item.gsr.toString()
        p0.tv_suhu.text = item.suhu.toString()
        p0.tv_tanggal.text = item.tanggal_perangkat
        p0.tv_waktu.text = item.waktu_perangkat
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }
}