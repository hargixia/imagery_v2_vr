package com.example.imagery_vr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.survey_jawaban

data class data_pertanyaan(val soal : String)

class adapter_survey (
    private val dataList: List<data_pertanyaan>,
    private val onOpsi1:(survey_jawaban)-> Unit,
    private val onOpsi2:(survey_jawaban)-> Unit
): RecyclerView.Adapter<adapter_survey.viewHolder>(){
    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemText    : TextView  = itemView.findViewById(R.id.sci_text_1)
        val itemOpsi1   : Button    = itemView.findViewById(R.id.sci_opsi_1)
        val itemOpsi2   : Button    = itemView.findViewById(R.id.sci_opsi_2)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val current = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_survey,parent,false)
        return viewHolder(current)
    }

    override fun onBindViewHolder(
        holder: viewHolder,
        position: Int
    ) {
        val currentItem = dataList[position]
        holder.itemText.text = currentItem.soal
    }

    override fun getItemCount(): Int = dataList.size
}