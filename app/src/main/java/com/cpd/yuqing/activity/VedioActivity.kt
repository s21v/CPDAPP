package com.cpd.yuqing.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cpd.yuqing.R
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.VideoView
import kotlin.properties.Delegates

/**
 * Created by s21v on 2018/2/9.
 */
class VedioActivity : AppCompatActivity() {
    private lateinit var vedioView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isInit = Vitamio.isInitialized(applicationContext)
        Log.i(TAG, "VitamioLib is init : $isInit")
        setContentView(R.layout.activity_vedio_content)
        vedioView = findViewById(R.id.vv)
        vedioView.setVideoURI(Uri.parse("http://v.cpd.com.cn:80/video/wenhua/shangchuanshenhe/2018/02/07/48B189F5A58B47539C186EE71AD2633B0.mp4"))
    }

    override fun onResume() {
        super.onResume()
        vedioView.start()
    }

    override fun onPause() {
        super.onPause()
        vedioView.pause()
    }

    companion object {
        val TAG = VedioActivity::class.java.simpleName!!
    }
}