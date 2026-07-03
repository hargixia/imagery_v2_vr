package com.example.imagery_vr.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.perangkat_akses_list
import com.example.imagery_vr.models.perangkat_data_list
import com.example.imagery_vr.ui.perangkat_data_2

class adapter_perangkat_data (
    private val dataList: List<perangkat_akses_list>
) : RecyclerView.Adapter<adapter_perangkat_data.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val card    : CardView = itemView.findViewById(R.id.cpd1_card)
        val tv_pos  : TextView = itemView.findViewById(R.id.cpd1_tv_pos)
        val tv_tgl  : TextView = itemView.findViewById(R.id.cpd1_tv_tanggal)
    }

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_item_perangkat_data1,p0,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        p0: ViewHolder,
        p1: Int
    ) {
        val item = dataList[p1]
        p0.tv_pos.text = item.pos.toString()

        p0.card.setOnClickListener {
            val intent = Intent(p0.itemView.context, perangkat_data_2::class.java)
                .apply {
                    putExtra("ida", item.id)
                }
            p0.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataList.count()
    }
}