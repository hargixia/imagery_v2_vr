package com.example.imagery_vr.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.imagery_vr.R
import com.example.imagery_vr.models.kuisoner_cek
import com.example.imagery_vr.models.materi_items
import com.example.imagery_vr.models.materi_list
import com.example.imagery_vr.support.api_services
import com.example.imagery_vr.support.encryption
import com.example.imagery_vr.support.retrofit
import com.example.imagery_vr.ui.Kuisoner
import com.example.imagery_vr.ui.Materi_Detail
import com.google.android.material.card.MaterialCardView
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class adapter_materi (
    private val data        : List<materi_items>,
    private val user_id     : Int
): RecyclerView.Adapter<adapter_materi.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card            : CardView = itemView.findViewById(R.id.cim_card)
        val tv_judul        : TextView = itemView.findViewById(R.id.cim_tv_judul)
        val tv_deskripsi    : TextView = itemView.findViewById(R.id.cim_tv_deskripsi)

        val apis            = retrofit.instance.create(api_services::class.java)
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
        val item = data[position]
        holder.tv_judul.text        = item.judul
        holder.tv_deskripsi.text    = item.desc
        holder.card.setOnClickListener {

            val req = "kc>>" + user_id + ">>" + item.id
            val enc = encryption().encob64(req)
            holder.apis.getKuisonerCek(enc).enqueue(object : Callback<kuisoner_cek>{
                override fun onResponse(
                    call: Call<kuisoner_cek?>,
                    response: Response<kuisoner_cek?>
                ) {
                    if(response.isSuccessful){
                        val isData = response.body()
                        if(isData?.status == 0){
                            val intent = Intent(holder.itemView.context, Kuisoner::class.java).apply {
                                putExtra("mode","PreTest")
                                putExtra("m_id",item.id )
                                putExtra("m_judul",item.judul)
                            }
                            holder.itemView.context.startActivity(intent)
                        }else if(isData?.status == 1){
                            val intent = Intent(holder.itemView.context, Materi_Detail::class.java)
                                .apply {
                                    putExtra("m_id",item.id)
                                    putExtra("m_judul",item.judul)
                                    putExtra("m_desc",item.desc)
                                }
                            holder.itemView.context.startActivity(intent)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<kuisoner_cek?>,
                    t: Throwable
                ) {
                    Toast.makeText(holder.itemView.context,"Error => ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}