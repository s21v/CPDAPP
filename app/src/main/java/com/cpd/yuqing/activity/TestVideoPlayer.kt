package com.cpd.yuqing.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cpd.yuqing.R
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.MediaController
import io.vov.vitamio.widget.VideoView
import kotlinx.android.synthetic.main.testvideoplayer.*

/**
 * Created by s21v on 2018/3/21.
 */
class TestVideoPlayer : AppCompatActivity() {
    val url = "http://v.cpd.com.cn:80/video/wenhua/shangchuanshenhe/2018/03/01/6770784076BC47cb8C8CFA3961DC06DE0.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testvideoplayer)
        if (Vitamio.isInitialized(this)) {
            videoView.setVideoURI(Uri.parse(url))
            val mMediaController = MediaController(this)

            videoView.setVideoQuality(VideoView.DRAWING_CACHE_QUALITY_HIGH)
            videoView.setMediaController(mMediaController)
        }
    }

    override fun onStart() {
        super.onStart()
        videoView.pause()
    }
}