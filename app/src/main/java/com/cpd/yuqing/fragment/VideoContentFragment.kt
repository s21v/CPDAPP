package com.cpd.yuqing.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.cpd.yuqing.BR
import com.cpd.yuqing.R
import com.cpd.yuqing.databinding.FragmentVedioContentBinding
import com.cpd.yuqing.db.vo.video.News
import com.cpd.yuqing.util.DensityUtils
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.MediaController
import io.vov.vitamio.widget.VideoView
import kotlinx.android.synthetic.main.fragment_vedio_content.*
import java.io.IOException
import java.nio.charset.Charset

/**
 * 视频新闻内容页
 * Created by s21v on 2018/3/19.
 */
class VideoContentFragment : Fragment() {
    private lateinit var news: News
    //当前是否为全屏
    private var mIsFullScreen: Boolean = false
    private lateinit var mMediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        news = if (savedInstanceState != null)
            savedInstanceState.getParcelable("news")
        else
            arguments.getParcelable("news")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentVedioContentBinding>(inflater, R.layout.fragment_vedio_content, container, false)
        dataBinding.setVariable(BR.news, news)
        dataBinding.executePendingBindings()
        return dataBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 检查设备是否支持Vitamio
        if (Vitamio.isInitialized(context)) {
            mMediaController = MediaController(activity, true, videoContainer)
            mMediaController.visibility = View.GONE
            Glide.with(this).load(news.thumbIconUrl).into(videoThumbnail)
            // 播放按钮
            videoStartButton.setOnClickListener {
                videoStartButton.visibility = View.GONE
                videoView.setVideoURI(Uri.parse(news.videoUrl))
                videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0f)
                videoView.setMediaController(mMediaController)
                videoThumbnail.visibility = View.GONE
            }
            // onComplete监听
            videoView.setOnCompletionListener {
                //停止播放
                videoView.stopPlayback()
                videoStartButton.visibility = View.VISIBLE
            }
        }
        //设置WebView
        contentWebView.isVerticalFadingEdgeEnabled = false
        contentWebView.setBackgroundColor(0)
        //设置webView,支持JavaScript
        val webSettings = contentWebView.settings
        webSettings.javaScriptEnabled = true
        //添加本地css文件
        val css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/videoContent.css\">"
        val baseUrl = "http://v.cpd.com.cn/"
        news.content = css + news.content
        //设置webview 显示内容 loadDataWithBaseURL()可以避免中文乱码
        contentWebView.loadDataWithBaseURL(baseUrl, news.content, "text/html", "utf-8", null)
    }

    override fun onStart() {
        super.onStart()
        //加载本地js文件
        contentWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String): Unit = try {
                val sb = StringBuilder()
                context.assets.open("content.js")   //放在之前的生命周期方法中会报错 context is null
                        .buffered()
                        .reader(Charset.forName("utf-8"))
                        .use {
                            it.forEachLine {
                                sb.append(it)
                            }
                        }
                view.loadUrl("javascript:" + sb.toString())
                //切换白天\夜间模式
                val contentSettings = context.getSharedPreferences("contentSetting", MODE_PRIVATE)
                val isNightMode = contentSettings.getBoolean("isNightMode", false)
                if (!isNightMode) {
                } else view.loadUrl(String.format("javascript:switchNightMode(%b)", isNightMode))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            mIsFullScreen = true
            //隐藏actionBar
            (activity as AppCompatActivity).supportActionBar?.hide()
            //隐藏状态栏
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)


            //调整mFlVideoGroup布局参数
            val params = LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            videoContainer.layoutParams = params
            videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0f)
        } else {
            mIsFullScreen = false
            //显示actionBar
            (activity as AppCompatActivity).supportActionBar?.show()
            //显示状态栏
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    DensityUtils.dp2px(context, 234f))
            videoContainer.layoutParams = params
            videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0f)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("news", news)
    }

    fun onBackPressed(): Boolean {
        if (mIsFullScreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mMediaController.setFullScreenIconState(false)
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (videoView != null) {
            //清除缓存
            videoView.destroyDrawingCache()
            //停止播放
            videoView.stopPlayback()
        }
    }

    companion object {
        fun getInstance(news: News): VideoContentFragment {
            val bundle = Bundle()
            bundle.putParcelable("news", news)
            val fragment = VideoContentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}