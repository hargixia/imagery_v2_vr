package com.example.imagery_vr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.kuisoner_jawaban
import com.example.imagery_vr.models.kuisoner_pertanyaan_items

class adapter_kuisoner2 (
    private val data        : List<kuisoner_pertanyaan_items>,
    private val id_user     : Int,
    private val id_materi   : Int,
    private val dataJawaban :(List<kuisoner_jawaban>) -> Unit
): RecyclerView.Adapter<adapter_kuisoner2.viewHolder>(){

    private val jawab : MutableList<kuisoner_jawaban> = mutableListOf()

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemNum     : TextView      = itemView.findViewById(R.id.sci2_num)
        val itemText    : TextView      = itemView.findViewById(R.id.sci2_text_1)
        val itemOpsiG   : RadioGroup    = itemView.findViewById(R.id.sci2_rg_1)
        val opsi1       : RadioButton   = itemView.findViewById(R.id.sci2_rg_op1)
        val opsi2       : RadioButton   = itemView.findViewById(R.id.sci2_rg_op2)
        val opsi3       : RadioButton   = itemView.findViewById(R.id.sci2_rg_op3)
        val opsi4       : RadioButton   = itemView.findViewById(R.id.sci2_rg_op4)
        val opsi5       : RadioButton   = itemView.findViewById(R.id.sci2_rg_op5)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val current = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_soal_2,parent,false)
        return viewHolder(current)
    }

    override fun onBindViewHolder(
        holder: viewHolder,
        position: Int
    ) {
        val currentItem = data[position]
        var opsi = 0

        if(jawab.size < data.size){
            jawab.addAll(List(data.size - jawab.size){ kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)})
        }
        holder.itemNum.text = currentItem.no.toString()
        holder.itemText.text = currentItem.soal

        holder.opsi1.setOnClickListener {
            opsi = 0
            jawab[position] = kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
        }

        holder.opsi2.setOnClickListener {
            opsi = 1
            jawab[position] = kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
        }

        holder.opsi3.setOnClickListener {
            opsi = 2
            jawab[position] = kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
            //Toast.makeText(holder.itemView.context, ">> $position " + jawab[position].value, Toast.LENGTH_SHORT).show()
        }

        holder.opsi4.setOnClickListener {
            opsi = 3
            jawab[position] = kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
            //Toast.makeText(holder.itemView.context, ">> $position " + jawab[position].value, Toast.LENGTH_SHORT).show()
        }

        holder.opsi5.setOnClickListener {
            opsi = 4
            jawab[position] = kuisoner_jawaban(currentItem.id,id_user,id_materi,currentItem.id,opsi)
            dataJawaban(jawab)
            //Toast.makeText(holder.itemView.context, ">> $position " + jawab[position].value, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}