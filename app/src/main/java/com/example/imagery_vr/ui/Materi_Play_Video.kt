package com.example.imagery_vr.ui

import android.media.session.MediaController
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.imagery_vr.R

class Materi_Play_Video : AppCompatActivity() {

    private lateinit var tv_desc        : TextView
    private lateinit var tv_count_down  : TextView
    private lateinit var tv1            : TextView
    private lateinit var videoplayer    : PlayerView
    private lateinit var exoplayer      : ExoPlayer

    lateinit var mediaControl           : android.widget.MediaController
    private lateinit var mediasesion    : androidx.media3.session.MediaSession

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val desc        = intent.getStringExtra("md2_desc")
        val video_url   = intent.getStringExtra("md2_video")
        exoplayer = ExoPlayer.Builder(this).build()
        mediasesion = androidx.media3.session.MediaSession.Builder(this,exoplayer).build()

        tv_desc         = findViewById(R.id.m_pv_desc)
        tv_count_down   = findViewById(R.id.m_pv_countdown)
        tv1             = findViewById(R.id.m_pv_tv1)
        videoplayer     = findViewById(R.id.m_pv_videoplayer)

        tv_desc.setText(desc)
        videoplayer.player = exoplayer
        val url_parse = Uri.parse(video_url)
        val mediaItem = MediaItem.fromUri(url_parse)
        exoplayer.setMediaItem(mediaItem)
        exoplayer.repeatMode = ExoPlayer.REPEAT_MODE_OFF
        exoplayer.prepare()

        startCountdown(11000,1000)
    }

    private fun startCountdown(totalTimeMillis: Long, intervalMillis: Long) {
        countDownTimer?.cancel() // Cancel any existing timer

        countDownTimer = object : CountDownTimer(totalTimeMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tv_count_down.text = " $secondsLeft"
            }

            override fun onFinish() {
                tv_desc.visibility          = View.GONE
                tv_count_down.visibility    = View.GONE
                tv1.visibility              = View.GONE
                val vp_layout               = videoplayer.layoutParams as LinearLayout.LayoutParams
                vp_layout.height = ViewGroup.LayoutParams.MATCH_PARENT
                videoplayer.layoutParams = vp_layout

                WindowCompat.setDecorFitsSystemWindows(window,false)
                WindowInsetsControllerCompat(window,window.decorView).let { controller->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }.start()
    }

}