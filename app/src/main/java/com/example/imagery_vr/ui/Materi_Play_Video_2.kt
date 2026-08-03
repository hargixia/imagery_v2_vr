package com.example.imagery_vr.ui

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imagery_vr.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class Materi_Play_Video_2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_materi_play_video2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val player = findViewById<YouTubePlayerView>(R.id.m_pv2_player)
        lifecycle.addObserver(player)
        player.enableAutomaticInitialization = false
        var ui_default : DefaultPlayerUiController

        player.initialize(object : AbstractYouTubePlayerListener(){

            override fun onReady(youTubePlayer: YouTubePlayer) {

                val videoId = "MpSMkRdgemw"
                youTubePlayer.loadVideo(videoId, 0f)

                ui_default = DefaultPlayerUiController(player,youTubePlayer)
                //ui_default.showVideoTitle(false)
                //ui_default.showSeekBar(false)
                //ui_default.showYouTubeButton(false)
                player.setCustomPlayerUi(ui_default.rootView)

                super.onReady(youTubePlayer)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                if (state == PlayerConstants.PlayerState.ENDED){
                    Toast.makeText(this@Materi_Play_Video_2,"End", Toast.LENGTH_LONG).show()
                }
            }
        }, true,
            IFramePlayerOptions.Builder(this@Materi_Play_Video_2)
                .controls(0)
                .autoplay(1)
                .ivLoadPolicy(1)
                .ccLoadPolicy(1)
                .fullscreen(1)
                .build())

    }
}