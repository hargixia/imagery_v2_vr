package com.example.imagery_vr.ui

import android.os.Bundle
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import org.w3c.dom.Text

class Materi_Play_teks : AppCompatActivity() {

    private lateinit var tx_judul       : TextView
    private lateinit var tx_desc        : TextView
    private lateinit var tx_isi         : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_teks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tx_judul    = findViewById(R.id.mpt_judul)
        tx_desc     = findViewById(R.id.mpt_deskripsi)
        tx_isi      = findViewById(R.id.mpt_isi)

        val i_judul     = intent.getStringExtra("md2_judul")
        val i_desc      = intent.getStringExtra("md2_desc")
        val i_isi       = intent.getStringExtra("md2_isi")

        tx_judul.text   = i_judul
        tx_desc.text    = i_desc
        tx_isi.text     = HtmlCompat.fromHtml(i_isi.toString(),FROM_HTML_MODE_LEGACY)
    }
}