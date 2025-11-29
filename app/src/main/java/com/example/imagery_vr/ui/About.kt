package com.example.imagery_vr.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R

class About : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var base_url       = "https://snow-turtle-672937.hostingersite.com/storage/"
    private val audioUrls = arrayOf(
        base_url + "audio/intro/2/1.mp3",
        base_url + "audio/intro/2/2.mp3",
        base_url + "audio/intro/2/3.mp3",
        base_url + "audio/intro/2/4.mp3",
        base_url + "audio/intro/2/5.mp3",
    )
    private var currentTrackIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playNextTrack()
    }

    private fun playNextTrack() {
        if (currentTrackIndex >= audioUrls.size) {
            // Jika sudah, reset index ke 0 untuk memulai dari track pertama
            currentTrackIndex = 0
            Toast.makeText(this, "Mengulang playlist dari awal", Toast.LENGTH_SHORT).show()
            // TIDAK perlu memanggil releaseMediaPlayer() di sini
        }

        // Pastikan MediaPlayer di-reset atau dibuat baru
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.reset() // Reset instance yang ada
        }

        try {
            //

            val url = audioUrls[currentTrackIndex]
            mediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url) // Tetapkan URL audio

                // Listener yang dipanggil ketika persiapan sudah selesai
                setOnPreparedListener { mp ->
                    mp.start() // Mulai pemutaran
                }

                // Listener yang dipanggil ketika pemutaran audio selesai
                setOnCompletionListener {
                    currentTrackIndex++
                    playNextTrack() // Pindah ke track berikutnya
                }

                prepareAsync() // Persiapan asynchronous (non-blocking) untuk streaming
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            Log.d("AudioPlayer", "Error saat memutar audio: ${e.message}")
            e.printStackTrace()
            currentTrackIndex++
            playNextTrack() // Coba track berikutnya jika ada error
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }
}