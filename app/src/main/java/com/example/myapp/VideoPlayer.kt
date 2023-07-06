package com.example.myapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView

class VideoPlayer : AppCompatActivity() {

    private lateinit var videoView : VideoView
    private lateinit var mediaControl : MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        videoView = findViewById(R.id.video_view)

        /*val videoUri = Uri.parse()
        videoView.setVideoURI(videoUri)

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)

        videoView.start()*/

    }
}