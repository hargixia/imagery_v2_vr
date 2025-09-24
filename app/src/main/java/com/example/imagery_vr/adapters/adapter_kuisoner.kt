package com.example.imagery_vr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.adapters.adapter_survey.viewHolder
import com.example.imagery_vr.models.kuisoner_jawaban
import com.example.imagery_vr.models.kuisoner_pertanyaan
import com.example.imagery_vr.models.survey_jawaban

class adapter_kuisoner (
    private val data        : List<kuisoner_pertanyaan>,
    private val id_user     : Int,
    private val id_materi   : Int,
    private val dataJawaban :(List<kuisoner_jawaban>) -> Unit
): RecyclerView.Adapter<adapter_kuisoner.viewHolder>(){

    private val jawab : MutableList<kuisoner_jawaban> = mutableListOf()

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
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
        val currentItem = data[position]
        var opsi = 0

        if(jawab.size < data.size){
            jawab.addAll(List(data.size - jawab.size){ kuisoner_jawaban(id_user,id_materi,currentItem.id,opsi)})
        }

        holder.itemText.text = currentItem.pertanyaan

        holder.opsi1.setOnClickListener {
            opsi = 1
            jawab[position] = kuisoner_jawaban(id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
        }

        holder.opsi2.setOnClickListener {
            opsi = 2
            jawab[position] = kuisoner_jawaban(id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
        }

        holder.opsi3.setOnClickListener {
            opsi = 3
            jawab[position] = kuisoner_jawaban(id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
            //Toast.makeText(holder.itemView.context, ">> $position " + jawab[position].value, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}