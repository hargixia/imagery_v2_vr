package com.example.imagery_vr.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.MainActivity
import com.example.imagery_vr.R
import java.time.LocalDate

@Suppress("DEPRECATION")
class Dashboard : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var base_url       = "https://snow-turtle-672937.hostingersite.com/storage/"
    private val audioUrls = arrayOf(
        base_url + "audio/intro/1/1.mp3",
        base_url + "audio/intro/1/2.mp3",
        base_url + "audio/intro/1/3.mp3",
    )
    private var currentTrackIndex = 0

    private lateinit var db_tv1             : TextView
    private lateinit var btn_logout         : Button
    private lateinit var btn_survey         : Button
    private lateinit var btn_materi1        : Button
    private lateinit var btn_materi2        : Button
    private lateinit var btn_about          : Button
    private lateinit var ds                 : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ds              = getSharedPreferences("IMGV1", Context.MODE_PRIVATE)
        val ds_nama     = ds.getString("nama","user")
        val ds_id       = ds.getInt("user_id",0)
        val survey_count= ds.getInt("survey_count",0)

        db_tv1          = findViewById(R.id.db_tv1)
        btn_logout      = findViewById(R.id.db_logout_btn)
        btn_materi1      = findViewById(R.id.db_btn_materi1)
        btn_materi2      = findViewById(R.id.db_btn_materi2)
        btn_about       = findViewById(R.id.db_btn_about)

        db_tv1.text = "Nama User : $ds_nama"

        playNextTrack()

        if (survey_count > 2){
            startActivity(Intent(this@Dashboard, Survey::class.java))
            releaseMediaPlayer()
        }

        btn_logout.setOnClickListener {
            logout()
        }

        btn_materi1.setOnClickListener {
            val intent = Intent(this@Dashboard, materi::class.java).apply {
                putExtra("App","App1")
                putExtra("AppVal",1)
            }
            startActivity(intent)
            releaseMediaPlayer()
        }

        btn_materi2.setOnClickListener {
            val intent = Intent(this@Dashboard, materi::class.java).apply {
                putExtra("App","App2")
                putExtra("AppVal",2)
            }
            startActivity(intent)
            releaseMediaPlayer()
        }

        btn_about.setOnClickListener {
            startActivity(Intent(this@Dashboard,About::class.java))
            releaseMediaPlayer()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
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

    fun logout(){
        val dse         = ds.edit()
        dse.clear()
        dse.apply()
        startActivity(Intent(this@Dashboard, MainActivity::class.java))
        finish()
    }
}