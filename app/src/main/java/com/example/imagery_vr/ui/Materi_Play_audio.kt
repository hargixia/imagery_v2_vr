package com.example.imagery_vr.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R

class Materi_Play_audio : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    // Ganti dengan URL audio Anda yang sebenarnya
    private var audioUrl = "URL_AUDIO_ANDA_UNTUK_SINGLE_PLAY"

    private lateinit var tx_judul       : TextView
    private lateinit var tx_desc        : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_audio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val i_judul     = intent.getStringExtra("md2_judul")
        val i_desc      = intent.getStringExtra("md2_desc")
        val i_isi       = intent.getStringExtra("md2_isi")

        tx_judul    = findViewById(R.id.mpa_judul)
        tx_desc     = findViewById(R.id.mpa_deskripsi)

        tx_judul.text   = i_judul
        tx_desc.text    = i_desc
        audioUrl = i_isi.toString()

        startAudioPlaybackAndFinishOnCompletion()
    }

    private fun startAudioPlaybackAndFinishOnCompletion() {
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.apply {
                // Konfigurasi atribut audio
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                // Tetapkan sumber data dari URL
                setDataSource(audioUrl)

                // Listener saat persiapan selesai (siap untuk dimainkan)
                setOnPreparedListener { mp ->
                    mp.start()
                    Toast.makeText(this@Materi_Play_audio, "Memutar Audio...", Toast.LENGTH_SHORT).show()
                }

                // *** Kunci Implementasi: Selesai lalu Tutup Activity ***
                // Listener saat pemutaran selesai
                setOnCompletionListener {
                    // Lepaskan sumber daya MediaPlayer
                    releaseMediaPlayer()

                    // Tutup Activity (berpindah kembali ke Activity sebelumnya)
                    finish()
                }

                // Listener jika terjadi error
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "Error saat pemutaran: $what, $extra")
                    Toast.makeText(this@Materi_Play_audio, "Gagal memutar audio.", Toast.LENGTH_LONG).show()
                    releaseMediaPlayer()
                    finish()
                    true // Mengembalikan true menunjukkan error telah ditangani
                }

                // Persiapan Asinkron (penting untuk URL/streaming)
                prepareAsync()
            }

        } catch (e: Exception) {
            Log.e("AudioPlayer", "Exception: ${e.message}")
            e.printStackTrace()
            releaseMediaPlayer()
            finish()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop() // Hentikan jika masih memutar
            }
            release() // Lepaskan sumber daya
        }
        mediaPlayer = null
    }

    // 3. Menghentikan Audio Jika Activity Ditutup Paksa (Bukan Selesai Secara Normal)
    override fun onDestroy() {
        super.onDestroy()
        // Panggilan keamanan untuk memastikan sumber daya dilepaskan
        // jika Activity ditutup oleh pengguna/sistem sebelum lagu selesai.
        releaseMediaPlayer()
    }
}