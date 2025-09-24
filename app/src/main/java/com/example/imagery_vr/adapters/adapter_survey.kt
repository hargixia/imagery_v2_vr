package com.example.imagery_vr.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.survey_jawaban
import com.example.imagery_vr.models.survey_soal

class adapter_survey (
    private val dataList    : List<survey_soal>,
    private val id_user     : Int,
    private val dataJawaban :(List<survey_jawaban>) -> Unit
): RecyclerView.Adapter<adapter_survey.viewHolder>(){

    private val jawab : MutableList<survey_jawaban> = mutableListOf()

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemText    : TextView      = itemView.findViewById(R.id.sci_text_1)
        val itemOpsiG   : RadioGroup    = itemView.findViewById(R.id.sci_rg_1)
        val opsi1       : RadioButton   = itemView.findViewById(R.id.sci_rg_op1)
        val opsi2       : RadioButton   = itemView.findViewById(R.id.sci_rg_op2)
        val opsi3       : RadioButton   = itemView.findViewById(R.id.sci_rg_op3)
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
        var data = 0

        if(jawab.size < dataList.size){
            jawab.addAll(List(dataList.size - jawab.size){ survey_jawaban(id_user,currentItem.id,data)})
        }

        holder.itemText.text = currentItem.pertanyaan

        holder.opsi1.setOnClickListener {
            data = 1
            jawab[position] = survey_jawaban(id_user,currentItem.id,data)
            dataJawaban(jawab)
        }

        holder.opsi2.setOnClickListener {
            data = 2
            jawab[position] = survey_jawaban(id_user,currentItem.id,data)
            dataJawaban(jawab)
        }

        holder.opsi3.setOnClickListener {
            data = 3
            jawab[position] = survey_jawaban(id_user,currentItem.id,data)
            dataJawaban(jawab)
            //Toast.makeText(holder.itemView.context, ">> $position " + jawab[position].value, Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int = dataList.size
}