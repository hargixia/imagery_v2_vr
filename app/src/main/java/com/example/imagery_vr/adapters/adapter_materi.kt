package com.example.imagery_vr.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.materi_list
import com.example.imagery_vr.ui.Materi_Detail
import com.google.android.material.card.MaterialCardView
import org.w3c.dom.Text

class adapter_materi (
    private val data : List<materi_list>
): RecyclerView.Adapter<adapter_materi.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card            : CardView = itemView.findViewById(R.id.cim_card)
        val tv_judul        : TextView = itemView.findViewById(R.id.cim_tv_judul)
        val tv_deskripsi    : TextView = itemView.findViewById(R.id.cim_tv_deskripsi)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_materi,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = data[0].res[position]
        holder.tv_judul.text        = item.judul
        holder.tv_deskripsi.text    = item.desc
        holder.card.setOnClickListener {
            val intent = Intent(holder.itemView.context, Materi_Detail::class.java)
                .apply {
                    putExtra("m_id",item.id)
                    putExtra("m_judul",item.judul)
                    putExtra("m_desc",item.desc)
                }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}